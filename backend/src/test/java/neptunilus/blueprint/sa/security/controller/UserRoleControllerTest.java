package neptunilus.blueprint.sa.security.controller;

import neptunilus.blueprint.sa.security.controller.out.UserRoleResponse;
import neptunilus.blueprint.sa.security.exception.UserRoleNotFoundException;
import neptunilus.blueprint.sa.security.model.UserRole;
import neptunilus.blueprint.sa.security.service.UserRoleService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserRoleController.class)
@WithMockUser
@AutoConfigureMockMvc(addFilters = false) // TODO remove
public class UserRoleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRoleService userRoleService;

    @MockBean
    @Qualifier("securityMapper")
    private ModelMapper modelMapper;

    @Test
    public void testSearch_ShouldReturnEmptyPageIfNothingFound() throws Exception {
        doReturn(Page.empty()).when(this.userRoleService).find(any(Pageable.class));

        this.mockMvc
                .perform(
                        get("/userrole")
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty());

        verify(this.userRoleService).find(any(Pageable.class));

        verifyNoMoreInteractions(this.userRoleService, this.modelMapper);
    }

    @Test
    public void testSearch_ShouldReturnCategoriesIfFound() throws Exception {
        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);

        UserRole userRole = new UserRole("myUserRole", Collections.emptySet());
        Page<UserRole> userRoles = new PageImpl<>(List.of(userRole));
        doReturn(userRoles).when(this.userRoleService).find(any(Pageable.class));

        UserRoleResponse userRoleResponse = new UserRoleResponse();
        userRoleResponse.setId(UUID.randomUUID());
        userRoleResponse.setName("myCategory");
        doReturn(userRoleResponse).when(this.modelMapper).map(userRole, UserRoleResponse.class);

        this.mockMvc
                .perform(
                        get("/userrole")
                                .param("page", "2")
                                .param("size", "10")
                                .param("sort", "name,desc")
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].id").value(equalTo(userRoleResponse.getId().toString())))
                .andExpect(jsonPath("$.content[0].name").value(equalTo(userRoleResponse.getName())));

        verify(this.userRoleService).find(pageableCaptor.capture());
        assertThat(pageableCaptor.getValue()).extracting("pageNumber").isEqualTo(2);
        assertThat(pageableCaptor.getValue()).extracting("pageSize").isEqualTo(10);
        assertThat(pageableCaptor.getValue().getSort().getOrderFor("name")).extracting("direction").isEqualTo(Sort.Direction.DESC);
        verify(this.modelMapper).map(userRole, UserRoleResponse.class);

        verifyNoMoreInteractions(this.userRoleService, this.modelMapper);
    }

    @Test
    public void testGet_ShouldReturn404IfNotFound() throws Exception {
        UUID id = UUID.randomUUID();

        doThrow(new UserRoleNotFoundException(String.format("no user role found with id '%s'", id)))
                .when(this.userRoleService).get(id);

        this.mockMvc
                .perform(
                        get("/userrole/{id}", id)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[0]").value(equalTo(String.format("no user role found with id '%s'", id.toString()))));

        verify(this.userRoleService).get(id);

        verifyNoMoreInteractions(this.userRoleService, this.modelMapper);
    }

    @Test
    public void testGet_ShouldReturn400IfIdIsInvalid() throws Exception {
        String id = "testId";

        this.mockMvc
                .perform(
                        get("/userrole/{id}", id)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[0]").value(containsStringIgnoringCase("invalid")))
                .andExpect(jsonPath("$.errors[0]").value(containsStringIgnoringCase(id)));

        verifyNoInteractions(this.userRoleService, this.modelMapper);
    }

    @Test
    public void testGet_ShouldReturnUserRoleIfEverythingIsFine() throws Exception {
        UUID id = UUID.randomUUID();

        UserRole userRole = new UserRole("myUserRole", Collections.emptySet());
        userRole.setId(id);
        doReturn(userRole).when(this.userRoleService).get(id);

        UserRoleResponse userRoleResponse = new UserRoleResponse();
        userRoleResponse.setId(id);
        userRoleResponse.setName("myUserRole");
        doReturn(userRoleResponse).when(this.modelMapper).map(userRole, UserRoleResponse.class);

        this.mockMvc
                .perform(
                        get("/userrole/{id}", id)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(equalTo(userRoleResponse.getId().toString())))
                .andExpect(jsonPath("$.name").value(equalTo(userRoleResponse.getName())));

        verify(this.userRoleService).get(id);
        verify(this.modelMapper).map(userRole, UserRoleResponse.class);

        verifyNoMoreInteractions(this.userRoleService, this.modelMapper);
    }
}
