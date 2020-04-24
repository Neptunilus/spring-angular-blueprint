package neptunilus.blueprint.sa.security.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * Thrown if something went wrong during JWT authentication or authorization.
 */
public class JwtAuthenticationException extends AuthenticationException {

    public JwtAuthenticationException(final String msg) {
        super(msg);
    }

    public JwtAuthenticationException(final String msg, final Throwable t) {
        super(msg, t);
    }

}
