package neptunilus.blueprint.sa.security.service.impl;

import neptunilus.blueprint.sa.security.authentication.AuthenticatedUser;
import neptunilus.blueprint.sa.security.model.User;
import neptunilus.blueprint.sa.security.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Specific implementation of {@link UserDetailsService}.
 */
@Service
public class AuthenticatedUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public AuthenticatedUserDetailsService(final UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public AuthenticatedUser loadUserByUsername(final String username) throws UsernameNotFoundException {
        final User user = this.userRepository.findOneByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException(String.format("no user with email '%s' found", username)));

        return new AuthenticatedUser(user);
    }

}
