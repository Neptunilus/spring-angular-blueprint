package neptunilus.blueprint.sa.security.controller.out;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.UUID;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

/**
 * A user representation sent to the outside.
 */
@Data
@JsonInclude(NON_NULL)
public class UserResponse {

    private UUID id;

    private String email;

    private UserRoleResponse role;

}
