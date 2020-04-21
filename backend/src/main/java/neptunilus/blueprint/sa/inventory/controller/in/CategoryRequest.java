package neptunilus.blueprint.sa.inventory.controller.in;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.UUID;

/**
 * A category representation sent from the outside.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CategoryRequest {

    private UUID id;

    @NotEmpty(message = "category name must not be empty")
    @Size(max = 100, message = "category name length must be <= 100")
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
