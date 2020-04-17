package neptunilus.blueprint.sa.security.exception;

/**
 * Thrown by {@link neptunilus.blueprint.sa.security.service.UserService} if a user already exists during adding.
 */
public class UserAlreadyExistsException extends RuntimeException {

    public UserAlreadyExistsException(final String message) {
        super(message);
    }

}
