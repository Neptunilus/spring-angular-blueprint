package neptunilus.blueprint.sa.security.service.impl;

import neptunilus.blueprint.sa.security.exception.UserRoleNotFoundException;
import neptunilus.blueprint.sa.security.model.UserRole;
import neptunilus.blueprint.sa.security.repository.UserRoleRepository;
import neptunilus.blueprint.sa.security.service.UserRoleService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Concrete implementation of {@link UserRoleService}.
 */
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
    public UserRole get(final String name) throws UserRoleNotFoundException {
        if (name == null || name.isBlank()) {
            throw new UserRoleNotFoundException("no user role with empty name possible");
        }

        final Optional<UserRole> userRole = this.userRoleRepository.findOneByName(name);
        return userRole.orElseThrow(() -> new UserRoleNotFoundException(String.format("no user role found with name '%s'", name)));
    }
}
