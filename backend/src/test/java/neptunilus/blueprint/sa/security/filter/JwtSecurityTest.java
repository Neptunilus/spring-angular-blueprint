package neptunilus.blueprint.sa.security.filter;

import neptunilus.blueprint.sa.inventory.service.CategoryService;
import neptunilus.blueprint.sa.security.authentication.AuthenticatedUser;
import neptunilus.blueprint.sa.security.model.Authority;
import neptunilus.blueprint.sa.security.model.User;
import neptunilus.blueprint.sa.security.model.UserRole;
import neptunilus.blueprint.sa.security.service.impl.AuthenticatedUserDetailsService;
import neptunilus.blueprint.sa.security.util.JwtUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Set;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class JwtSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @MockBean
    private AuthenticatedUserDetailsService authenticatedUserDetailsService;

    @Autowired
    private JwtUtils jwtUtils;

    @SpyBean
    private CategoryService categoryService;

    @Test
    public void testLogin_ShouldDenyWithUnknownUser() throws Exception {
        String username = "me@mail.xy";
        String password = "password";
        String body = "{ \"username\": \"" + username + "\", \"password\": \"" + password + "\" }";

        doThrow(new UsernameNotFoundException("user not found")).when(this.authenticatedUserDetailsService).loadUserByUsername(username);

        this.mockMvc
                .perform(
                        post("/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(body)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$").doesNotExist());
    }

    @Test
    public void testLogin_ShouldDenyWithWrongPassword() throws Exception {
        String username = "me@mail.xy";
        String password = "password";
        String body = "{ \"username\": \"" + username + "\", \"password\": \"" + password + "\" }";

        UserRole role = new UserRole("myRole", Set.of(Authority.CREATE_CATEGORY));
        role.setId(UUID.randomUUID());

        String passwordEncoded = this.passwordEncoder.encode("myPassword");
        User user = new User(username, passwordEncoded, role);
        user.setId(UUID.randomUUID());

        AuthenticatedUser authenticatedUser = new AuthenticatedUser(user);
        doReturn(authenticatedUser).when(this.authenticatedUserDetailsService).loadUserByUsername(username);

        this.mockMvc
                .perform(
                        post("/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(body)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$").doesNotExist());
    }

    @Test
    public void testLogin_ShouldAllowWithValidCredentials() throws Exception {
        String username = "me@mail.xy";
        String password = "password";
        String body = "{ \"username\": \"" + username + "\", \"password\": \"" + password + "\" }";

        UserRole role = new UserRole("myRole", Set.of(Authority.CREATE_CATEGORY));
        role.setId(UUID.randomUUID());

        String passwordEncoded = this.passwordEncoder.encode(password);
        User user = new User(username, passwordEncoded, role);
        user.setId(UUID.randomUUID());

        AuthenticatedUser authenticatedUser = new AuthenticatedUser(user);
        doReturn(authenticatedUser).when(this.authenticatedUserDetailsService).loadUserByUsername(username);

        this.mockMvc
                .perform(
                        post("/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(body)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.access_token").isNotEmpty());
    }

    @Test
    public void testRequest_ShouldDenyWithExpiredToken() throws Exception {
        String secret = "dGlCEqrpmkP2NqWwB-mTXASixxlSstPPW9nrazlsS358YETji9g5VapuX72IKi2eoZg4cY6yRZ0Ft7GmZGz-dw";
        JwtUtils jwtUtils = new JwtUtils(secret, "myIssuer", 0);

        UserRole role = new UserRole("myRole", Set.of(Authority.CREATE_CATEGORY));
        role.setId(UUID.randomUUID());

        User user = new User("me@mail.xy", "password", role);
        user.setId(UUID.randomUUID());

        AuthenticatedUser authenticatedUser = new AuthenticatedUser(user);

        String token = jwtUtils.generate(authenticatedUser);

        this.mockMvc
                .perform(
                        get("/category")
                                .accept(MediaType.APPLICATION_JSON)
                                .header(HttpHeaders.AUTHORIZATION, String.format("Bearer %s", token))
                )
                .andDo(print())
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$").doesNotExist());

        verifyNoInteractions(this.categoryService);
    }

    @Test
    public void testRequest_ShouldDenyWithWrongTokenSignature() throws Exception {
        String secret = "XXXdGlCEqrpmkP2NqWwB-mTXASixxlSstPPW9nrazlsS358YETji9g5VapuX72IKi2eoZg4cY6yRZ0Ft7GmZGz-dw";
        JwtUtils jwtUtils = new JwtUtils(secret, "myIssuer", 3600);

        UserRole role = new UserRole("myRole", Set.of(Authority.CREATE_CATEGORY));
        role.setId(UUID.randomUUID());

        User user = new User("me@mail.xy", "password", role);
        user.setId(UUID.randomUUID());

        AuthenticatedUser authenticatedUser = new AuthenticatedUser(user);

        String token = jwtUtils.generate(authenticatedUser);

        this.mockMvc
                .perform(
                        get("/category")
                                .accept(MediaType.APPLICATION_JSON)
                                .header(HttpHeaders.AUTHORIZATION, String.format("Bearer %s", token))
                )
                .andDo(print())
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$").doesNotExist());

        verifyNoInteractions(this.categoryService);
    }

    @Test
    public void testRequest_ShouldDenyWithMissingHeader() throws Exception {
        this.mockMvc
                .perform(
                        get("/category")
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$").doesNotExist());

        verifyNoInteractions(this.categoryService);
    }

    @Test
    public void testRequest_ShouldAllowWithCorrectTokenAndAuthority() throws Exception {
        UserRole role = new UserRole("myRole", Set.of(Authority.READ_CATEGORY));
        role.setId(UUID.randomUUID());

        User user = new User("me@mail.xy", "password", role);
        user.setId(UUID.randomUUID());

        AuthenticatedUser authenticatedUser = new AuthenticatedUser(user);
        doReturn(authenticatedUser).when(this.authenticatedUserDetailsService).loadUserByUsername(user.getEmail());

        String token = this.jwtUtils.generate(authenticatedUser);

        this.mockMvc
                .perform(
                        get("/category")
                                .accept(MediaType.APPLICATION_JSON)
                                .header(HttpHeaders.AUTHORIZATION, String.format("Bearer %s", token))
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists());

        verify(this.categoryService).find(isNull(), eq(false), any(Pageable.class));
    }

}
