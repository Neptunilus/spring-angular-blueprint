package neptunilus.blueprint.sa.security.exception;

/**
 * Thrown by {@link neptunilus.blueprint.sa.security.service.UserService} if a user was not found.
 */
public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(final String message) {
        super(message);
    }

}
