package neptunilus.blueprint.sa.security.controller.in;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

/**
 * A user update request.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserUpdateRequest {

    @Email(message = "user email must be a valid one")
    @NotEmpty(message = "user email must not be empty")
    @Size(max = 100, message = "user email length must be <= 100")
    private String email;

    private String password;

    @Valid
    private UserRoleReferenceRequest role;

    public String getEmail() {
        return this.email;
    }

    public void setEmail(final String email) {
        this.email = email;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(final String password) {
        this.password = password;
    }

    public UserRoleReferenceRequest getRole() {
        return this.role;
    }

    public void setRole(final UserRoleReferenceRequest role) {
        this.role = role;
    }

}
