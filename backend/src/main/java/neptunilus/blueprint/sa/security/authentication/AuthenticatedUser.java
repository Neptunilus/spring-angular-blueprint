package neptunilus.blueprint.sa.security.authentication;

import neptunilus.blueprint.sa.security.model.User;
import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

/**
 * An authenticated user used in security context.
 */
public class AuthenticatedUser implements UserDetails, CredentialsContainer {

    private final User user;

    public AuthenticatedUser(final User user) {
        this.user = user;
    }

    public User getUser() {
        return this.user;
    }

    @Override
    public void eraseCredentials() {
        this.user.setPassword(null);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.user.getRole().getAuthorities();
    }

    @Override
    public String getPassword() {
        return this.user.getPassword();
    }

    @Override
    public String getUsername() {
        return this.user.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

}
