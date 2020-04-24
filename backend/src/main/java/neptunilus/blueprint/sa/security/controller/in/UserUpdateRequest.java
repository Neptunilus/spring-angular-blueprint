package neptunilus.blueprint.sa.security.controller.in;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

/**
 * A user update request.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserUpdateRequest {

    @Email(message = "user email must be a valid one")
    @NotEmpty(message = "user email must not be empty")
    @Size(max = 100, message = "user email length must be <= 100")
    private String email;

    private String password;

    @Valid
    private UserRoleReferenceRequest role;

}
