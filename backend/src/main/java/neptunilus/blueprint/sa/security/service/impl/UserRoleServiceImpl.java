package neptunilus.blueprint.sa.security.service.impl;

import neptunilus.blueprint.sa.security.exception.UserRoleNotFoundException;
import neptunilus.blueprint.sa.security.model.UserRole;
import neptunilus.blueprint.sa.security.repository.UserRoleRepository;
import neptunilus.blueprint.sa.security.service.UserRoleService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.Optional;
import java.util.UUID;

/**
 * Concrete implementation of {@link UserRoleService}.
 */
@Service
public class UserRoleServiceImpl implements UserRoleService {

    private final UserRoleRepository userRoleRepository;

    public UserRoleServiceImpl(final UserRoleRepository userRoleRepository) {
        this.userRoleRepository = userRoleRepository;
    }

    @Transactional(readOnly = true)
    @Override
    public Page<UserRole> find(final Pageable pageable) {
        return this.userRoleRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    @Override
    public UserRole get(final UUID id) throws UserRoleNotFoundException {
        Assert.notNull(id, "id must not be null");

        final Optional<UserRole> userRole = this.userRoleRepository.findById(id);
        return userRole.orElseThrow(() -> new UserRoleNotFoundException(String.format("no user role found with id '%s'", id)));
    }
}
