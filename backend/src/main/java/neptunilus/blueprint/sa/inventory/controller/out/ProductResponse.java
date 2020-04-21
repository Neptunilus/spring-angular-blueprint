package neptunilus.blueprint.sa.inventory.controller.out;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.UUID;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

/**
 * A product response.
 */
@JsonInclude(NON_NULL)
public class ProductResponse {

    private UUID id;

    private String name;

    private CategoryResponse category;

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

    public CategoryResponse getCategory() {
        return this.category;
    }

    public void setCategory(final CategoryResponse category) {
        this.category = category;
    }

}
