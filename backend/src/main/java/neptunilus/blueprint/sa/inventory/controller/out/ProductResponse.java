package neptunilus.blueprint.sa.inventory.controller.out;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.UUID;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

/**
 * A product response.
 */
@Data
@JsonInclude(NON_NULL)
public class ProductResponse {

    private UUID id;

    private String name;

    private CategoryResponse category;

}
