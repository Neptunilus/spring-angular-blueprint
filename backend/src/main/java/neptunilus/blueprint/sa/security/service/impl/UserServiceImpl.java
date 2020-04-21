package neptunilus.blueprint.sa.security.service.impl;

import neptunilus.blueprint.sa.security.exception.UserAlreadyExistsException;
import neptunilus.blueprint.sa.security.exception.UserNotFoundException;
import neptunilus.blueprint.sa.security.model.User;
import neptunilus.blueprint.sa.security.model.UserRole;
import neptunilus.blueprint.sa.security.repository.UserRepository;
import neptunilus.blueprint.sa.security.service.UserRoleService;
import neptunilus.blueprint.sa.security.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.Collections;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * Concrete implementation of {@link UserService}.
 */
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserRoleService userRoleService;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(final UserRepository userRepository,
                           final UserRoleService userRoleService,
                           final PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userRoleService = userRoleService;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    @Override
    public Page<User> find(final String search, final boolean strict, final Pageable pageable) {
        if (search == null || search.isBlank()) {
            return this.userRepository.findAll(pageable);
        }
        if (strict) {
            final Optional<User> user = this.userRepository.findOneByEmail(search);
            return user.isPresent() ? new PageImpl<>(Collections.singletonList(user.get())) : Page.empty();
        }
        return this.userRepository.findByEmailContainingIgnoreCase(search, pageable);
    }

    @Transactional(readOnly = true)
    @Override
    public User get(final UUID id) throws UserNotFoundException {
        Assert.notNull(id, "id must not be null");

        final Optional<User> user = this.userRepository.findById(id);
        return user.orElseThrow(() -> new UserNotFoundException(String.format("no user found with id '%s'", id)));
    }

    @Transactional
    @Override
    public UUID create(final User user) throws UserAlreadyExistsException {
        Assert.notNull(user, "user must not be null");
        assertUserWithEmailNotPresent(user.getEmail());

        final UserRole userRoleFetched = user.getRole() != null ?
                this.userRoleService.get(user.getRole().getId()) : null;
        final String passwordEncoded = this.passwordEncoder.encode(user.getPassword());

        User newUser = new User(user.getEmail(), passwordEncoded, userRoleFetched);
        newUser = this.userRepository.save(newUser);

        return newUser.getId();
    }

    @Transactional
    @Override
    public void update(final UUID id, final User update) throws UserNotFoundException {
        Assert.notNull(id, "id must not be null");
        Assert.notNull(update, "new data must not be null");

        final User existingUser = get(id);

        if (!Objects.equals(existingUser.getEmail(), update.getEmail())) {
            assertUserWithEmailNotPresent(update.getEmail());
        }

        final UserRole newUserRole = update.getRole() == null ?
                existingUser.getRole() : this.userRoleService.get(update.getRole().getId());
        final String passwordEncoded = update.getPassword() == null || update.getPassword().isBlank() ?
                existingUser.getPassword() : this.passwordEncoder.encode(update.getPassword());

        existingUser.setEmail(update.getEmail());
        existingUser.setPassword(passwordEncoded);
        existingUser.setRole(newUserRole);

        this.userRepository.save(existingUser);
    }

    @Transactional
    @Override
    public void delete(final UUID id) {
        if (id == null) {
            return;
        }

        final Optional<User> existingUser = this.userRepository.findById(id);
        existingUser.ifPresent(this.userRepository::delete);
    }

    private void assertUserWithEmailNotPresent(final String email) {
        final Optional<User> existingUser = this.userRepository.findOneByEmail(email);
        if (existingUser.isPresent()) {
            throw new UserAlreadyExistsException(String.format("user with email '%s' already exists", email));
        }
    }
}
