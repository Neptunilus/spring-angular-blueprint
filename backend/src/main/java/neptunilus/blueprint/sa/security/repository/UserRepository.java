package neptunilus.blueprint.sa.security.repository;

import neptunilus.blueprint.sa.security.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * The repository for the {@link User}s.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Returns the {@link User} with the given email or {@link Optional#empty()} if none found.
     *
     * @param email The email
     * @return The user
     */
    Optional<User> findOneByEmail(String email);

    /**
     * Returns all {@link User}s containing the search in email.
     *
     * @param search   The search
     * @param pageable The pagination information
     * @return The users
     */
    Page<User> findByEmailContainingIgnoreCase(String search, Pageable pageable);

}
