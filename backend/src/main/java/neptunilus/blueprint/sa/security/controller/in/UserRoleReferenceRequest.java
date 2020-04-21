package neptunilus.blueprint.sa.security.controller.in;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.validation.constraints.NotNull;
import java.util.UUID;

/**
 * A user role reference used in requests.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserRoleReferenceRequest {

    @NotNull(message = "referenced user role id must not be null")
    private UUID id;

    public UUID getId() {
        return this.id;
    }

    public void setId(final UUID id) {
        this.id = id;
    }

}
