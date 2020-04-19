package neptunilus.blueprint.sa.security.repository;

import neptunilus.blueprint.sa.security.model.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * The repository for the {@link UserRole}s.
 */
@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, UUID> {

    /**
     * Returns the {@link UserRole} with the given name or {@link Optional#empty()} if none found.
     *
     * @param name The name
     * @return The user role
     */
    Optional<UserRole> findOneByName(String name);

}
