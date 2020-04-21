package neptunilus.blueprint.sa.inventory.controller.in;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.UUID;

/**
 * A product create request.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProductCreateRequest {

    @NotEmpty(message = "product name must not be empty")
    @Size(max = 100, message = "product name length must be <= 100")
    private String name;

    @Valid
    private CategoryReferenceRequest category;

    public String getName() {
        return this.name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public CategoryReferenceRequest getCategory() {
        return this.category;
    }

    public void setCategory(final CategoryReferenceRequest category) {
        this.category = category;
    }

}
