package neptunilus.blueprint.sa.security.controller;

import neptunilus.blueprint.sa.security.authentication.AuthenticatedUser;
import neptunilus.blueprint.sa.security.model.Authority;
import neptunilus.blueprint.sa.security.model.User;
import neptunilus.blueprint.sa.security.model.UserRole;
import neptunilus.blueprint.sa.security.repository.UserRepository;
import neptunilus.blueprint.sa.security.service.UserService;
import neptunilus.blueprint.sa.security.service.impl.AuthenticatedUserDetailsService;
import neptunilus.blueprint.sa.security.util.JwtUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtUtils jwtUtils;

    @MockBean
    private AuthenticatedUserDetailsService authenticatedUserDetailsService;

    @SpyBean
    private UserService userService;

    @MockBean
    private UserRepository userRepository;

    @Test
    public void testUpdate_ShouldAllowWithUpdateAuthorityAndOtherUser() throws Exception {
        UserRole role = new UserRole("myRole", Set.of(Authority.UPDATE_USER));
        role.setId(UUID.randomUUID());

        User user = new User("me@mail.xy", "password", role);
        user.setId(UUID.randomUUID());

        AuthenticatedUser authenticatedUser = new AuthenticatedUser(user);
        doReturn(authenticatedUser).when(this.authenticatedUserDetailsService).loadUserByUsername(user.getEmail());

        UUID idToUpdate = UUID.randomUUID();
        String update = "{ \"email\": \"me@mail.xy\", \"password\": \"password2\" }";

        User userToUpdate = mock(User.class);
        doReturn(Optional.of(userToUpdate)).when(this.userRepository).findById(idToUpdate);
        doReturn(null).when(this.userRepository).save(userToUpdate);

        String token = this.jwtUtils.generate(authenticatedUser);

        this.mockMvc
                .perform(
                        put("/user/{id}", idToUpdate)
                                .accept(MediaType.APPLICATION_JSON)
                                .header(HttpHeaders.AUTHORIZATION, String.format("Bearer %s", token))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(update)
                )
                .andDo(print())
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$").doesNotExist());

        verify(this.userService).update(eq(idToUpdate), any(User.class));
    }

    @Test
    public void testUpdate_ShouldAllowWithUpdateAuthorityAndOwnUser() throws Exception {
        UserRole role = new UserRole("myRole", Set.of(Authority.UPDATE_USER));
        role.setId(UUID.randomUUID());

        User user = new User("me@mail.xy", "password", role);
        user.setId(UUID.randomUUID());

        AuthenticatedUser authenticatedUser = new AuthenticatedUser(user);
        doReturn(authenticatedUser).when(this.authenticatedUserDetailsService).loadUserByUsername(user.getEmail());

        UUID idToUpdate = user.getId();
        String update = "{ \"email\": \"me@mail.xy\", \"password\": \"password2\" }";

        User userToUpdate = mock(User.class);
        doReturn(Optional.of(userToUpdate)).when(this.userRepository).findById(idToUpdate);
        doReturn(null).when(this.userRepository).save(userToUpdate);

        String token = this.jwtUtils.generate(authenticatedUser);

        this.mockMvc
                .perform(
                        put("/user/{id}", idToUpdate)
                                .accept(MediaType.APPLICATION_JSON)
                                .header(HttpHeaders.AUTHORIZATION, String.format("Bearer %s", token))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(update)
                )
                .andDo(print())
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$").doesNotExist());

        verify(this.userService).update(eq(idToUpdate), any(User.class));
    }

    @Test
    public void testUpdate_ShouldAllowWithoutUpdateAuthorityButOwnUser() throws Exception {
        UserRole role = new UserRole("myRole", Set.of(Authority.READ_USER));
        role.setId(UUID.randomUUID());

        User user = new User("me@mail.xy", "password", role);
        user.setId(UUID.randomUUID());

        AuthenticatedUser authenticatedUser = new AuthenticatedUser(user);
        doReturn(authenticatedUser).when(this.authenticatedUserDetailsService).loadUserByUsername(user.getEmail());

        UUID idToUpdate = user.getId();
        String update = "{ \"email\": \"me@mail.xy\", \"password\": \"password2\" }";

        User userToUpdate = mock(User.class);
        doReturn(Optional.of(userToUpdate)).when(this.userRepository).findById(idToUpdate);
        doReturn(null).when(this.userRepository).save(userToUpdate);

        String token = this.jwtUtils.generate(authenticatedUser);

        this.mockMvc
                .perform(
                        put("/user/{id}", idToUpdate)
                                .accept(MediaType.APPLICATION_JSON)
                                .header(HttpHeaders.AUTHORIZATION, String.format("Bearer %s", token))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(update)
                )
                .andDo(print())
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$").doesNotExist());

        verify(this.userService).update(eq(idToUpdate), any(User.class));
    }

    @Test
    public void testUpdate_ShouldDenyWithoutUpdateAuthorityAndOtherUser() throws Exception {
        UserRole role = new UserRole("myRole", Set.of(Authority.READ_USER));
        role.setId(UUID.randomUUID());

        User user = new User("me@mail.xy", "password", role);
        user.setId(UUID.randomUUID());

        AuthenticatedUser authenticatedUser = new AuthenticatedUser(user);
        doReturn(authenticatedUser).when(this.authenticatedUserDetailsService).loadUserByUsername(user.getEmail());

        UUID idToUpdate = UUID.randomUUID();
        String update = "{ \"email\": \"me@mail.xy\", \"password\": \"password2\" }";

        User userToUpdate = mock(User.class);
        doReturn(Optional.of(userToUpdate)).when(this.userRepository).findById(idToUpdate);
        doReturn(null).when(this.userRepository).save(userToUpdate);

        String token = this.jwtUtils.generate(authenticatedUser);

        this.mockMvc
                .perform(
                        put("/user/{id}", idToUpdate)
                                .accept(MediaType.APPLICATION_JSON)
                                .header(HttpHeaders.AUTHORIZATION, String.format("Bearer %s", token))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(update)
                )
                .andDo(print())
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$").doesNotExist());

        verifyNoInteractions(this.userService);
    }

}
