package neptunilus.blueprint.sa.inventory.controller.in;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

/**
 * A product create request.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProductCreateRequest {

    @NotEmpty(message = "product name must not be empty")
    @Size(max = 100, message = "product name length must be <= 100")
    private String name;

    @Valid
    private CategoryReferenceRequest category;

}
