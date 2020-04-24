package neptunilus.blueprint.sa.security.controller.in;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.UUID;

/**
 * A user role reference used in requests.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserRoleReferenceRequest {

    @NotNull(message = "referenced user role id must not be null")
    private UUID id;

}
