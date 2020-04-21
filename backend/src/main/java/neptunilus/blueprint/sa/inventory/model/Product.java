package neptunilus.blueprint.sa.inventory.model;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.UUID;

/**
 * A product.
 */
@Entity
@Table(name = "product")
public class Product {

    /**
     * The unique identifier.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    /**
     * The name (cannot be empty).
     */
    @NotEmpty
    @Size(max = 100)
    @Column(unique = true)
    private String name;

    /**
     * The (optional) category.
     */
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    protected Product() {
        super();
    }

    public Product(final String name) {
        this(name, null);
    }

    public Product(final String name, final Category category) {
        this.name = name;
        this.category = category;
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

    public Category getCategory() {
        return this.category;
    }

    public void setCategory(final Category category) {
        this.category = category;
    }
}
