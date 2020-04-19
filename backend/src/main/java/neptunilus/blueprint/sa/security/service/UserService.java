package neptunilus.blueprint.sa.security.service;

import neptunilus.blueprint.sa.security.exception.UserAlreadyExistsException;
import neptunilus.blueprint.sa.security.exception.UserNotFoundException;
import neptunilus.blueprint.sa.security.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Service for handling with {@link User}s.
 */
@Service
public interface UserService {

    /**
     * Returns the available users.
     *
     * @param search   The (optional) search string for user email
     * @param strict   Flag, if the email should be exactly the same
     * @param pageable The pagination information
     * @return The categories
     */
    Page<User> find(String search, boolean strict, Pageable pageable);

    /**
     * Returns the user with the given id.
     *
     * @param id The id
     * @return The user
     * @throws UserNotFoundException If a user with the given id is not found
     */
    User get(UUID id) throws UserNotFoundException;

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
     * @param id     The id of the existing user to update
     * @param update The new data for the user
     * @throws UserNotFoundException      If a user with the given id is not found
     * @throws UserAlreadyExistsException If a user with the new email is already there
     */
    void update(UUID id, User update) throws UserNotFoundException, UserAlreadyExistsException;

    /**
     * Deletes an existing user (if in the system).
     *
     * @param id The id of the user to remove
     */
    void delete(UUID id);

}
