package neptunilus.blueprint.sa.product.model;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.UUID;

/**
 * A category.
 */
@Entity
@Table(name = "category")
public class Category {

    /**
     * The unique identifier.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    /**
     * The (unique) name (cannot be empty).
     */
    @NotEmpty
    @Size(max = 100)
    @Column(unique = true)
    private String name;

    protected Category() {
        super();
    }

    public Category(final String name) {
        this.name = name;
    }

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
