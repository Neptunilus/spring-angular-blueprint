package neptunilus.blueprint.sa.security.authentication;

import org.springframework.security.authentication.AbstractAuthenticationToken;

/**
 * A JWT authentication representation.
 */
public class JwtAuthenticationToken extends AbstractAuthenticationToken {

    private final AuthenticatedUser authenticatedUser;
    private String token;

    public JwtAuthenticationToken(final AuthenticatedUser authenticatedUser, final String token) {
        super(authenticatedUser.getAuthorities());
        this.authenticatedUser = authenticatedUser;
        this.token = token;
        setAuthenticated(true);
    }

    @Override
    public Object getPrincipal() {
        return this.authenticatedUser;
    }

    @Override
    public Object getCredentials() {
        return this.token;
    }

    @Override
    public void eraseCredentials() {
        super.eraseCredentials();
        this.token = null;
    }

}
