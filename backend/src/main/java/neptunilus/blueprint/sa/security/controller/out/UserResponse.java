package neptunilus.blueprint.sa.security.controller.out;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.UUID;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

/**
 * A user representation sent to the outside.
 */
@JsonInclude(NON_NULL)
public class UserResponse {

    private UUID id;

    private String email;

    private UserRoleResponse role;

    public UUID getId() {
        return this.id;
    }

    public void setId(final UUID id) {
        this.id = id;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(final String email) {
        this.email = email;
    }

    public UserRoleResponse getRole() {
        return this.role;
    }

    public void setRole(final UserRoleResponse role) {
        this.role = role;
    }

}
