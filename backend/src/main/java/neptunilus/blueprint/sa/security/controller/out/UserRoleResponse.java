package neptunilus.blueprint.sa.security.controller.out;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.UUID;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

/**
 * A user role representation sent to the outside.
 */
@Data
@JsonInclude(NON_NULL)
public class UserRoleResponse {

    private UUID id;

    private String name;

}
