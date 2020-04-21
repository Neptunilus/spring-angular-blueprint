package neptunilus.blueprint.sa.inventory.controller.in;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.UUID;

/**
 * A reference to an existing category used in requests.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CategoryReferenceRequest {

    @NotNull(message = "referenced category id must not be null")
    private UUID id;

    public UUID getId() {
        return this.id;
    }

    public void setId(final UUID id) {
        this.id = id;
    }

}
