package neptunilus.blueprint.sa.security.service;

import neptunilus.blueprint.sa.security.exception.UserRoleNotFoundException;
import neptunilus.blueprint.sa.security.model.UserRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Service for handling with {@link UserRole}s.
 */
@Service
public interface UserRoleService {

    /**
     * Returns the available user roles.
     *
     * @param pageable The pagination information
     * @return The user roles
     */
    Page<UserRole> find(Pageable pageable);

    /**
     * Returns the user role with the given name.
     *
     * @param name The name
     * @return The user role
     * @throws UserRoleNotFoundException If a user role with the given name is not found
     */
    UserRole get(String name) throws UserRoleNotFoundException;

}
