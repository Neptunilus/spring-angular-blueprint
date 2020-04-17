package neptunilus.blueprint.sa.security.service.impl;

import neptunilus.blueprint.sa.security.exception.UserAlreadyExistsException;
import neptunilus.blueprint.sa.security.exception.UserNotFoundException;
import neptunilus.blueprint.sa.security.model.User;
import neptunilus.blueprint.sa.security.model.UserRole;
import neptunilus.blueprint.sa.security.repository.UserRepository;
import neptunilus.blueprint.sa.security.service.UserRoleService;
import neptunilus.blueprint.sa.security.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.Optional;

/**
 * Concrete implementation of {@link UserService}.
 */
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
    public Page<User> find(final String search, final Pageable pageable) {
        if (search == null || search.isBlank()) {
            return this.userRepository.findAll(pageable);
        }
        return this.userRepository.findByEmailContainingIgnoreCase(search, pageable);
    }

    @Transactional(readOnly = true)
    @Override
    public User get(final String email) throws UserNotFoundException {
        if (email == null || email.isBlank()) {
            throw new UserNotFoundException("no user with empty email possible");
        }

        final Optional<User> user = this.userRepository.findOneByEmail(email);
        return user.orElseThrow(() -> new UserNotFoundException(String.format("no user found with email '%s'", email)));
    }

    @Transactional
    @Override
    public void create(final User user) throws UserAlreadyExistsException {
        Assert.notNull(user, "user must not be null");

        final Optional<User> existingUser = this.userRepository.findOneByEmail(user.getEmail());
        if (existingUser.isPresent()) {
            throw new UserAlreadyExistsException(String.format("user with email '%s' already exists", user.getEmail()));
        }

        final UserRole userRoleFetched = user.getRole() != null ?
                this.userRoleService.get(user.getRole().getName()) : null;
        final String passwordEncoded = this.passwordEncoder.encode(user.getPassword());

        final User newUser = new User(user.getEmail(), passwordEncoded, userRoleFetched);
        this.userRepository.save(newUser);
    }

    @Transactional
    @Override
    public void update(final String email, final User update) throws UserNotFoundException {
        Assert.notNull(update, "new data must not be null");

        final User existingUser = get(email);

        final UserRole newUserRole = update.getRole() != null ?
                this.userRoleService.get(update.getRole().getName()) : null;
        final String passwordEncoded = update.getPassword() == null || update.getPassword().isBlank() ?
                existingUser.getPassword() : this.passwordEncoder.encode(update.getPassword());

        existingUser.setEmail(update.getEmail());
        existingUser.setPassword(passwordEncoded);
        existingUser.setRole(newUserRole);

        this.userRepository.save(existingUser);
    }

    @Transactional
    @Override
    public void delete(final String email) {
        if (email == null || email.isBlank()) {
            return;
        }

        final Optional<User> existingUser = this.userRepository.findOneByEmail(email);
        existingUser.ifPresent(this.userRepository::delete);
    }
}
