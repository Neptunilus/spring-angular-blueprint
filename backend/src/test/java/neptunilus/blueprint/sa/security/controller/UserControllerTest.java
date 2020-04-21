package neptunilus.blueprint.sa.security.controller;

import neptunilus.blueprint.sa.security.controller.in.UserRequest;
import neptunilus.blueprint.sa.security.controller.out.UserResponse;
import neptunilus.blueprint.sa.security.controller.out.UserRoleResponse;
import neptunilus.blueprint.sa.security.exception.UserAlreadyExistsException;
import neptunilus.blueprint.sa.security.exception.UserNotFoundException;
import neptunilus.blueprint.sa.security.exception.UserRoleNotFoundException;
import neptunilus.blueprint.sa.security.model.User;
import neptunilus.blueprint.sa.security.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@WithMockUser
@AutoConfigureMockMvc(addFilters = false) // TODO remove
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    @Qualifier("securityMapper")
    private ModelMapper modelMapper;

    @Test
    public void testSearch_ShouldReturnEmptyPageIfNothingFound() throws Exception {
        String search = "search@search.xy";

        doReturn(Page.empty()).when(this.userService).find(eq(search), eq(false), any(Pageable.class));

        this.mockMvc
                .perform(
                        get("/user")
                                .param("search", search)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty());

        verify(this.userService).find(eq(search), eq(false), any(Pageable.class));

        verifyNoMoreInteractions(this.userService, this.modelMapper);
    }

    @Test
    public void testSearch_ShouldReturnUsersIfFoundWithoutSearch() throws Exception {
        User user = new User("test@mail.xy", "password", null);
        Page<User> users = new PageImpl<>(List.of(user));
        doReturn(users).when(this.userService).find(isNull(), eq(false), any(Pageable.class));

        UserRoleResponse userRoleResponse = new UserRoleResponse();
        userRoleResponse.setId(UUID.randomUUID());
        userRoleResponse.setName("myUserRole");

        UserResponse userResponse = new UserResponse();
        userResponse.setId(UUID.randomUUID());
        userResponse.setEmail("me@mail.xy");
        userResponse.setRole(userRoleResponse);

        doReturn(userResponse).when(this.modelMapper).map(user, UserResponse.class);

        this.mockMvc
                .perform(
                        get("/user")
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].id").value(equalTo(userResponse.getId().toString())))
                .andExpect(jsonPath("$.content[0].email").value(equalTo(userResponse.getEmail())))
                .andExpect(jsonPath("$.content[0].role.id").value(equalTo(userRoleResponse.getId().toString())))
                .andExpect(jsonPath("$.content[0].role.name").value(equalTo(userRoleResponse.getName())));

        verify(this.userService).find(isNull(), eq(false), any(Pageable.class));
        verify(this.modelMapper).map(user, UserResponse.class);

        verifyNoMoreInteractions(this.userService, this.modelMapper);
    }

    @Test
    public void testSearch_ShouldReturnUsersIfFoundWithSearchAndWithPagination() throws Exception {
        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        String search = "my@mail.xy";

        User user = new User("me@mailxy", "password", null);
        Page<User> users = new PageImpl<>(List.of(user));
        doReturn(users).when(this.userService).find(eq(search), eq(false), any(Pageable.class));

        UserRoleResponse userRoleResponse = new UserRoleResponse();
        userRoleResponse.setId(UUID.randomUUID());
        userRoleResponse.setName("myUserRole");

        UserResponse userResponse = new UserResponse();
        userResponse.setId(UUID.randomUUID());
        userResponse.setEmail("me@mail.xy");
        userResponse.setRole(userRoleResponse);

        doReturn(userResponse).when(this.modelMapper).map(user, UserResponse.class);

        this.mockMvc
                .perform(
                        get("/user")
                                .param("search", search)
                                .param("page", "2")
                                .param("size", "10")
                                .param("sort", "name,desc")
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].id").value(equalTo(userResponse.getId().toString())))
                .andExpect(jsonPath("$.content[0].email").value(equalTo(userResponse.getEmail())))
                .andExpect(jsonPath("$.content[0].role.id").value(equalTo(userRoleResponse.getId().toString())))
                .andExpect(jsonPath("$.content[0].role.name").value(equalTo(userRoleResponse.getName())));

        verify(this.userService).find(eq(search), eq(false), pageableCaptor.capture());
        assertThat(pageableCaptor.getValue()).extracting("pageNumber").isEqualTo(2);
        assertThat(pageableCaptor.getValue()).extracting("pageSize").isEqualTo(10);
        assertThat(pageableCaptor.getValue().getSort().getOrderFor("name")).extracting("direction").isEqualTo(Sort.Direction.DESC);
        verify(this.modelMapper).map(user, UserResponse.class);

        verifyNoMoreInteractions(this.userService, this.modelMapper);
    }

    @Test
    public void testGet_ShouldReturn404IfNotFound() throws Exception {
        UUID id = UUID.randomUUID();

        doThrow(new UserNotFoundException(String.format("no user found with id '%s'", id)))
                .when(this.userService).get(id);

        this.mockMvc
                .perform(
                        get("/user/{id}", id)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[0]").value(equalTo(String.format("no user found with id '%s'", id))));

        verify(this.userService).get(id);

        verifyNoMoreInteractions(this.userService, this.modelMapper);
    }

    @Test
    public void testGet_ShouldReturn400IfIdIsInvalid() throws Exception {
        String id = "testId";

        this.mockMvc
                .perform(
                        get("/user/{id}", id)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[0]").value(containsStringIgnoringCase("invalid")))
                .andExpect(jsonPath("$.errors[0]").value(containsStringIgnoringCase(id)));

        verifyNoInteractions(this.userService, this.modelMapper);
    }

    @Test
    public void testGet_ShouldReturnUserIfEverythingIsFine() throws Exception {
        UUID id = UUID.randomUUID();

        User user = new User("me@mail.xy", "password", null);
        doReturn(user).when(this.userService).get(id);

        UserResponse userResponse = new UserResponse();
        userResponse.setId(id);
        userResponse.setEmail("me@mail.xy");
        doReturn(userResponse).when(this.modelMapper).map(user, UserResponse.class);

        this.mockMvc
                .perform(
                        get("/user/{id}", id)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(equalTo(userResponse.getId().toString())))
                .andExpect(jsonPath("$.email").value(equalTo(userResponse.getEmail())))
                .andExpect(jsonPath("$.role").doesNotExist());

        verify(this.userService).get(id);
        verify(this.modelMapper).map(user, UserResponse.class);

        verifyNoMoreInteractions(this.userService, this.modelMapper);
    }

    @Test
    public void testCreate_ShouldReturn409IfUserAlreadyExists() throws Exception {
        ArgumentCaptor<UserRequest> userRequestCaptor = ArgumentCaptor.forClass(UserRequest.class);

        String body = "{ \"email\": \"me@mail.xy\", \"password\": \"password\" }";

        User user = new User("me@mail.xy", "password", null);
        doReturn(user).when(this.modelMapper).map(any(UserRequest.class), eq(User.class));

        doThrow(new UserAlreadyExistsException(String.format("user with email '%s' already exists", "me@mail.xy")))
                .when(this.userService).create(user);

        this.mockMvc
                .perform(
                        post("/user")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(body)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[0]").value(containsStringIgnoringCase("exists")))
                .andExpect(jsonPath("$.errors[0]").value(containsStringIgnoringCase("me@mail.xy")));

        verify(this.modelMapper).map(userRequestCaptor.capture(), eq(User.class));
        assertThat(userRequestCaptor.getValue()).extracting("id").isNull();
        assertThat(userRequestCaptor.getValue()).extracting("email").isEqualTo("me@mail.xy");
        verify(this.userService).create(user);

        verifyNoMoreInteractions(this.userService, this.modelMapper);
    }

    @Test
    public void testCreate_ShouldReturn404IfUserRoleNotFound() throws Exception {
        ArgumentCaptor<UserRequest> userRequestCaptor = ArgumentCaptor.forClass(UserRequest.class);

        UUID userRoleId = UUID.randomUUID();
        String body = "{ \"email\": \"me@mail.xy\", \"password\": \"password\", \"role\": { \"id\": \"" + userRoleId + "\" } }";

        User user = new User("me@mail.xy", "password", null);
        doReturn(user).when(this.modelMapper).map(any(UserRequest.class), eq(User.class));

        doThrow(new UserRoleNotFoundException(String.format("no user role found with id '%s'", userRoleId)))
                .when(this.userService).create(user);

        this.mockMvc
                .perform(
                        post("/user")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(body)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[0]").value(equalTo(String.format("no user role found with id '%s'", userRoleId))));

        verify(this.modelMapper).map(userRequestCaptor.capture(), eq(User.class));
        assertThat(userRequestCaptor.getValue()).extracting("id").isNull();
        assertThat(userRequestCaptor.getValue()).extracting("email").isEqualTo("me@mail.xy");
        assertThat(userRequestCaptor.getValue()).extracting("password").isEqualTo("password");
        assertThat(userRequestCaptor.getValue()).extracting("role").extracting("id").isEqualTo(userRoleId);
        verify(this.userService).create(user);

        verifyNoMoreInteractions(this.userService, this.modelMapper);
    }

    @Test
    public void testCreate_ShouldReturn400IfDataIsInvalid() throws Exception {
        String body = "{ \"email\": \"\", \"password\": \"password\" }";

        this.mockMvc
                .perform(
                        post("/user")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(body)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[0]").value(containsStringIgnoringCase("user email must not be empty")));

        verifyNoInteractions(this.userService, this.modelMapper);
    }

    @Test
    public void testCreate_ShouldReturn400IfDataIsInvalidInPersistence() throws Exception {
        String body = "{ \"email\": \"me@mail.xy\", \"password\": \"password\" }";

        User user = new User("", null, null);
        doReturn(user).when(this.modelMapper).map(any(UserRequest.class), eq(User.class));

        doThrow(new DataIntegrityViolationException("invalid"))
                .when(this.userService).create(user);

        this.mockMvc
                .perform(
                        post("/user")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(body)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[0]").value(containsStringIgnoringCase("invalid")));

        verify(this.modelMapper).map(any(UserRequest.class), eq(User.class));
        verify(this.userService).create(user);

        verifyNoMoreInteractions(this.userService, this.modelMapper);
    }

    @Test
    public void testCreate_ShouldCreateIfEverythingIsFine() throws Exception {
        ArgumentCaptor<UserRequest> userRequestCaptor = ArgumentCaptor.forClass(UserRequest.class);

        UUID userRoleId = UUID.randomUUID();
        String body = "{ \"email\": \"me@mail.xy\", \"password\": \"password\", \"role\": { \"id\": \"" + userRoleId + "\" } }";

        User user = new User("me@mail.xy", "password", null);
        doReturn(user).when(this.modelMapper).map(any(UserRequest.class), eq(User.class));

        UUID newId = UUID.randomUUID();
        doReturn(newId).when(this.userService).create(user);

        this.mockMvc
                .perform(
                        post("/user")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(body)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString(newId.toString())))
                .andExpect(jsonPath("$").doesNotExist());

        verify(this.modelMapper).map(userRequestCaptor.capture(), eq(User.class));
        assertThat(userRequestCaptor.getValue()).extracting("id").isNull();
        assertThat(userRequestCaptor.getValue()).extracting("email").isEqualTo("me@mail.xy");
        assertThat(userRequestCaptor.getValue()).extracting("password").isEqualTo("password");
        assertThat(userRequestCaptor.getValue()).extracting("role").extracting("id").isEqualTo(userRoleId);
        verify(this.userService).create(user);

        verifyNoMoreInteractions(this.userService, this.modelMapper);
    }

    @Test
    public void testUpdate_ShouldReturn404IfProductNotFound() throws Exception {
        ArgumentCaptor<UserRequest> userRequestCaptor = ArgumentCaptor.forClass(UserRequest.class);

        UUID id = UUID.randomUUID();
        String update = "{ \"email\": \"me@mail.xy\", \"password\": \"password\" }";

        User user = new User("me@mail.xy", "password", null);
        doReturn(user).when(this.modelMapper).map(any(UserRequest.class), eq(User.class));

        doThrow(new UserNotFoundException(String.format("no user found with id '%s'", id)))
                .when(this.userService).update(id, user);

        this.mockMvc
                .perform(
                        put("/user/{id}", id)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(update)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[0]").value(equalTo(String.format("no user found with id '%s'", id))));

        verify(this.modelMapper).map(userRequestCaptor.capture(), eq(User.class));
        assertThat(userRequestCaptor.getValue()).extracting("id").isNull();
        assertThat(userRequestCaptor.getValue()).extracting("email").isEqualTo("me@mail.xy");
        verify(this.userService).update(id, user);

        verifyNoMoreInteractions(this.userService, this.modelMapper);
    }

    @Test
    public void testUpdate_ShouldReturn409IfNewEmailAlreadyExists() throws Exception {
        ArgumentCaptor<UserRequest> userRequestCaptor = ArgumentCaptor.forClass(UserRequest.class);

        UUID id = UUID.randomUUID();
        String update = "{ \"email\": \"me@mail.xy\", \"password\": \"password\" }";

        User user = new User("me@mail.xy", "password", null);
        doReturn(user).when(this.modelMapper).map(any(UserRequest.class), eq(User.class));

        doThrow(new UserAlreadyExistsException(String.format("user with email '%s' already exists", "me@mail.xy")))
                .when(this.userService).update(id, user);

        this.mockMvc
                .perform(
                        put("/user/{id}", id)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(update)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[0]").value(equalTo(String.format("user with email '%s' already exists", "me@mail.xy"))));

        verify(this.modelMapper).map(userRequestCaptor.capture(), eq(User.class));
        assertThat(userRequestCaptor.getValue()).extracting("id").isNull();
        assertThat(userRequestCaptor.getValue()).extracting("email").isEqualTo("me@mail.xy");
        verify(this.userService).update(id, user);

        verifyNoMoreInteractions(this.userService, this.modelMapper);
    }

    @Test
    public void testUpdate_ShouldReturn400IfIdIsInvalid() throws Exception {
        String id = "testId";
        String update = "{ \"email\": \"me@mail.xy\" }";

        this.mockMvc
                .perform(
                        put("/user/{id}", id)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(update)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[0]").value(containsStringIgnoringCase("invalid")))
                .andExpect(jsonPath("$.errors[0]").value(containsStringIgnoringCase(id)));

        verifyNoInteractions(this.userService, this.modelMapper);
    }

    @Test
    public void testUpdate_ShouldReturn400IfDataIsInvalid() throws Exception {
        UUID id = UUID.randomUUID();
        String update = "{ \"email\": \"test\" , \"password\": \"password\" }";

        this.mockMvc
                .perform(
                        put("/user/{id}", id)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(update)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[0]").value(containsStringIgnoringCase("user email must be a valid one")));

        verifyNoInteractions(this.userService, this.modelMapper);
    }

    @Test
    public void testUpdate_ShouldReturn400IfDataIsInvalidInPersistence() throws Exception {
        UUID id = UUID.randomUUID();
        String update = "{ \"email\": \"me@mail.xy\", \"password\": \"password\" }";

        User user = new User("", "", null);
        doReturn(user).when(this.modelMapper).map(any(UserRequest.class), eq(User.class));

        doThrow(new DataIntegrityViolationException("invalid")).when(this.userService).update(id, user);

        this.mockMvc
                .perform(
                        put("/user/{id}", id)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(update)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[0]").value(containsStringIgnoringCase("invalid")));

        verify(this.modelMapper).map(any(UserRequest.class), eq(User.class));
        verify(this.userService).update(id, user);

        verifyNoMoreInteractions(this.userService, this.modelMapper);
    }

    @Test
    public void testUpdate_ShouldReturn404IfUserRoleNotFound() throws Exception {
        ArgumentCaptor<UserRequest> userRequestCaptor = ArgumentCaptor.forClass(UserRequest.class);

        UUID id = UUID.randomUUID();
        UUID userRoleId = UUID.randomUUID();
        String update = "{ \"email\": \"me@mail.xy\", \"password\": \"password\", \"role\": { \"id\": \"" + userRoleId + "\" } }";

        User user = new User("me@mail.xy", "password", null);
        doReturn(user).when(this.modelMapper).map(any(UserRequest.class), eq(User.class));

        doThrow(new UserRoleNotFoundException(String.format("no user role found with id '%s'", userRoleId)))
                .when(this.userService).update(id, user);

        this.mockMvc
                .perform(
                        put("/user/{id}", id)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(update)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[0]").value(equalTo(String.format("no user role found with id '%s'", userRoleId))));

        verify(this.modelMapper).map(userRequestCaptor.capture(), eq(User.class));
        assertThat(userRequestCaptor.getValue()).extracting("id").isNull();
        assertThat(userRequestCaptor.getValue()).extracting("email").isEqualTo("me@mail.xy");
        assertThat(userRequestCaptor.getValue()).extracting("role").extracting("id").isEqualTo(userRoleId);
        verify(this.userService).update(id, user);

        verifyNoMoreInteractions(this.userService, this.modelMapper);
    }

    @Test
    public void testUpdate_ShouldUpdateIfEverythingIsFine() throws Exception {
        ArgumentCaptor<UserRequest> userRequestCaptor = ArgumentCaptor.forClass(UserRequest.class);

        UUID id = UUID.randomUUID();
        String update = "{ \"email\": \"me@mail.xy\", \"password\": \"password\" }";

        User user = new User("me@mail.xy", "password", null);
        doReturn(user).when(this.modelMapper).map(any(UserRequest.class), eq(User.class));

        doNothing().when(this.userService).update(id, user);

        this.mockMvc
                .perform(
                        put("/user/{id}", id)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(update)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$").doesNotExist());

        verify(this.modelMapper).map(userRequestCaptor.capture(), eq(User.class));
        assertThat(userRequestCaptor.getValue()).extracting("id").isNull();
        assertThat(userRequestCaptor.getValue()).extracting("email").isEqualTo("me@mail.xy");
        verify(this.userService).update(id, user);

        verifyNoMoreInteractions(this.userService, this.modelMapper);
    }

    @Test
    public void testDelete_ShouldDelete() throws Exception {
        UUID id = UUID.randomUUID();

        this.mockMvc
                .perform(
                        delete("/user/{id}", id)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$").doesNotExist());

        verify(this.userService).delete(id);

        verifyNoMoreInteractions(this.userService, this.modelMapper);
    }
}
