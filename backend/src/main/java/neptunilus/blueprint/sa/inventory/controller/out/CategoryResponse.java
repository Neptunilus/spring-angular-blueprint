package neptunilus.blueprint.sa.inventory.controller.out;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.UUID;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

/**
 * A category response.
 */
@Data
@JsonInclude(NON_NULL)
public class CategoryResponse {

    private UUID id;

    private String name;

}
