package neptunilus.blueprint.sa.security.controller.out;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.UUID;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

/**
 * A user role representation sent to the outside.
 */
@JsonInclude(NON_NULL)
public class UserRoleResponse {

    private UUID id;

    private String name;

    public UUID getId() {
        return this.id;
    }

    public void setId(final UUID id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(final String name) {
        this.name = name;
    }

}
