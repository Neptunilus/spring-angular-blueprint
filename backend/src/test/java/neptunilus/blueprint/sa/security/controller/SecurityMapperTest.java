package neptunilus.blueprint.sa.security.controller;

import neptunilus.blueprint.sa.common.configuration.MappingConfiguration;
import neptunilus.blueprint.sa.security.controller.in.UserRequest;
import neptunilus.blueprint.sa.security.controller.in.UserRoleRequest;
import neptunilus.blueprint.sa.security.controller.out.UserResponse;
import neptunilus.blueprint.sa.security.controller.out.UserRoleResponse;
import neptunilus.blueprint.sa.security.model.Authority;
import neptunilus.blueprint.sa.security.model.User;
import neptunilus.blueprint.sa.security.model.UserRole;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;

import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class SecurityMapperTest {

    private final ModelMapper securityMapper = new MappingConfiguration().modelMapper();

    @Test
    public void testMap_ShouldMapUserRoleRequestToUserRoleCorrectly() {
        UUID userRoleId = UUID.randomUUID();

        UserRoleRequest userRoleRequest = new UserRoleRequest();
        userRoleRequest.setId(userRoleId);

        UserRole userRole = this.securityMapper.map(userRoleRequest, UserRole.class);

        assertThat(userRole).extracting("id").isEqualTo(userRoleId);
        assertThat(userRole).extracting("name").isNull();
        assertThat(userRole).extracting("authorities").asInstanceOf(InstanceOfAssertFactories.ITERABLE).isEmpty();
    }

    @Test
    public void testMap_ShouldMapUserRoleToUserRoleResponseCorrectly() {
        UUID userRoleId = UUID.randomUUID();
        String userRoleName = "userRoleName";

        UserRole userRole = new UserRole(userRoleName, Set.of(Authority.CREATE_CATEGORY));
        userRole.setId(userRoleId);

        UserRoleResponse userRoleResponse = this.securityMapper.map(userRole, UserRoleResponse.class);

        assertThat(userRoleResponse).extracting("id").isEqualTo(userRoleId);
        assertThat(userRoleResponse).extracting("name").isEqualTo(userRoleName);
    }

    @Test
    public void testMap_ShouldMapUserRequestToUserCorrectly() {
        UUID userRoleId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        String userEmail = "me@mail.xy";
        String userPassword = "password";

        UserRoleRequest userRoleRequest = new UserRoleRequest();
        userRoleRequest.setId(userRoleId);

        UserRequest userRequest = new UserRequest();
        userRequest.setId(userId);
        userRequest.setEmail(userEmail);
        userRequest.setPassword(userPassword);
        userRequest.setRole(userRoleRequest);

        User user = this.securityMapper.map(userRequest, User.class);

        assertThat(user).extracting("id").isEqualTo(userId);
        assertThat(user).extracting("email").isEqualTo(userEmail);
        assertThat(user).extracting("password").isEqualTo(userPassword);
        assertThat(user).extracting("role").extracting("id").isEqualTo(userRoleId);
        assertThat(user).extracting("role").extracting("name").isNull();
        assertThat(user).extracting("role").extracting("authorities").asInstanceOf(InstanceOfAssertFactories.ITERABLE).isEmpty();
    }

    @Test
    public void testMap_ShouldMapUserToUserResponseCorrectly() {
        UUID userRoleId = UUID.randomUUID();
        String userRoleName = "categoryName";
        UUID userId = UUID.randomUUID();
        String userEmail = "me@mail.xy";

        UserRole userRole = new UserRole(userRoleName, Set.of(Authority.CREATE_CATEGORY));
        userRole.setId(userRoleId);

        User user = new User(userEmail, "password", userRole);
        user.setId(userId);

        UserResponse userResponse = this.securityMapper.map(user, UserResponse.class);

        assertThat(userResponse).extracting("id").isEqualTo(userId);
        assertThat(userResponse).extracting("email").isEqualTo(userEmail);
        assertThat(userResponse).extracting("role").extracting("id").isEqualTo(userRoleId);
        assertThat(userResponse).extracting("role").extracting("name").isEqualTo(userRoleName);
    }
}
