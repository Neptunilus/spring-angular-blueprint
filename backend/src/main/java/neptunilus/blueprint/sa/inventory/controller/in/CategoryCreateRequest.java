package neptunilus.blueprint.sa.inventory.controller.in;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.UUID;

/**
 * A category create request.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CategoryCreateRequest {

    @NotEmpty(message = "category name must not be empty")
    @Size(max = 100, message = "category name length must be <= 100")
    private String name;

    public String getName() {
        return this.name;
    }

    public void setName(final String name) {
        this.name = name;
    }

}
