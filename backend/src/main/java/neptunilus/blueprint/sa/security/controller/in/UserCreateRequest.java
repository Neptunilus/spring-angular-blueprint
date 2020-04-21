package neptunilus.blueprint.sa.security.controller.in;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * A user create request.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserCreateRequest {

    @Email(message = "user email must be a valid one")
    @NotEmpty(message = "user email must not be empty")
    @Size(max = 100, message = "user email length must be <= 100")
    private String email;

    @NotEmpty(message = "user password must not be empty")
    @Size(max = 200, message = "user password length must be <= 200")
    private String password;

    @Valid
    @NotNull(message = "user role must not be null")
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