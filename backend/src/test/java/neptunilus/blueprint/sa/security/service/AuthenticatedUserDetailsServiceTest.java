package neptunilus.blueprint.sa.security.service;

import neptunilus.blueprint.sa.security.authentication.AuthenticatedUser;
import neptunilus.blueprint.sa.security.model.Authority;
import neptunilus.blueprint.sa.security.model.User;
import neptunilus.blueprint.sa.security.model.UserRole;
import neptunilus.blueprint.sa.security.repository.UserRepository;
import neptunilus.blueprint.sa.security.service.impl.AuthenticatedUserDetailsService;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthenticatedUserDetailsServiceTest {

    private final UserRepository userRepository;
    private final AuthenticatedUserDetailsService authenticatedUserDetailsService;

    AuthenticatedUserDetailsServiceTest(@Mock final UserRepository userRepository) {
        this.userRepository = userRepository;
        this.authenticatedUserDetailsService = new AuthenticatedUserDetailsService(userRepository);
    }

    @Test
    public void testLoadUserByUsername_ShouldThrowExceptionIfNotFound() {
        String username = "me@mail.xy";

        doReturn(Optional.empty()).when(this.userRepository).findOneByEmail(username);

        assertThatExceptionOfType(UsernameNotFoundException.class)
                .isThrownBy(() -> this.authenticatedUserDetailsService.loadUserByUsername(username))
                .withMessageContainingAll("no", "user", username);

        verify(this.userRepository).findOneByEmail(username);
        verifyNoMoreInteractions(this.userRepository);
    }

    @Test
    public void testLoadUserByUsername_ShouldReturnCorrectUserDetailsIfFound() {
        String username = "me@mail.xy";

        String email = "me@mail.xy";
        String password = "password";
        UserRole role = new UserRole("myRole", Set.of(Authority.CREATE_CATEGORY, Authority.UPDATE_USER));
        User user = new User(email, password, role);

        doReturn(Optional.of(user)).when(this.userRepository).findOneByEmail(email);

        AuthenticatedUser authenticatedUser = this.authenticatedUserDetailsService.loadUserByUsername(username);

        assertThat(authenticatedUser).extracting("username").isEqualTo(email);
        assertThat(authenticatedUser).extracting("password").isEqualTo(password);
        assertThat(authenticatedUser).extracting("user").extracting("role").isSameAs(role);
        assertThat(authenticatedUser).extracting("authorities").asInstanceOf(InstanceOfAssertFactories.ITERABLE)
                .containsExactlyInAnyOrder(Authority.CREATE_CATEGORY, Authority.UPDATE_USER);

        verify(this.userRepository).findOneByEmail(username);
        verifyNoMoreInteractions(this.userRepository);
    }

}
