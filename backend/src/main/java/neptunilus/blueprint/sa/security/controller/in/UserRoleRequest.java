package neptunilus.blueprint.sa.security.controller.in;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.UUID;

/**
 * A user role representation sent from the outside.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserRoleRequest {

    private UUID id;

    public UUID getId() {
        return this.id;
    }

    public void setId(final UUID id) {
        this.id = id;
    }

}
