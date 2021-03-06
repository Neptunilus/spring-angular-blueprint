package neptunilus.blueprint.sa.inventory.controller.in;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

/**
 * A category create request.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CategoryCreateRequest {

    @NotEmpty(message = "category name must not be empty")
    @Size(max = 100, message = "category name length must be <= 100")
    private String name;

}
