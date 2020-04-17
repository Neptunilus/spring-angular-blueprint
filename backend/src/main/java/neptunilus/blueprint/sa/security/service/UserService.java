package neptunilus.blueprint.sa.security.service;

import neptunilus.blueprint.sa.security.exception.UserAlreadyExistsException;
import neptunilus.blueprint.sa.security.exception.UserNotFoundException;
import neptunilus.blueprint.sa.security.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Service for handling with {@link User}s.
 */
@Service
public interface UserService {

    /**
     * Returns the available users.
     *
     * @param search   The (optional) search string for user email.
     * @param pageable The pagination information
     * @return The categories
     */
    Page<User> find(String search, Pageable pageable);

    /**
     * Returns the user with the given email.
     *
     * @param email The email
     * @return The user
     * @throws UserNotFoundException If a user with the given email is not found
     */
    User get(String email) throws UserNotFoundException;

    /**
     * Creates a new user.
     *
     * @param user The user to add
     * @throws UserAlreadyExistsException If a user with the given email is already there
     */
    void create(User user) throws UserAlreadyExistsException;

    /**
     * Updates an existing user.
     *
     * @param email  The email of the existing user to update
     * @param update The new data for the user
     * @throws UserNotFoundException If a user with the given email is not found
     */
    void update(String email, User update) throws UserNotFoundException;

    /**
     * Deletes an existing user (if in the system).
     *
     * @param email The email of the user to remove
     */
    void delete(String email);

}
