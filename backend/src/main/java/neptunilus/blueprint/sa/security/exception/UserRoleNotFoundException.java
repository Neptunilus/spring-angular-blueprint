package neptunilus.blueprint.sa.security.exception;

/**
 * Thrown by {@link neptunilus.blueprint.sa.security.service.UserRoleService} if a user role was not found.
 */
public class UserRoleNotFoundException extends RuntimeException {

    public UserRoleNotFoundException(final String message) {
        super(message);
    }

}
