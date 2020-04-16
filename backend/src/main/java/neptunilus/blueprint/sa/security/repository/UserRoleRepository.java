package neptunilus.blueprint.sa.security.repository;

import neptunilus.blueprint.sa.security.model.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * The repository for the {@link UserRole}s.
 */
@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, Long> {

    // nothing special
}
