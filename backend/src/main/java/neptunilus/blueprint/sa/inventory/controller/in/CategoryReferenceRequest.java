package neptunilus.blueprint.sa.inventory.controller.in;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.UUID;

/**
 * A reference to an existing category used in requests.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CategoryReferenceRequest {

    @NotNull(message = "referenced category id must not be null")
    private UUID id;

}
