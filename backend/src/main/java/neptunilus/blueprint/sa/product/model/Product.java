package neptunilus.blueprint.sa.product.model;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

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
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "product_generator")
    @SequenceGenerator(name = "product_generator", sequenceName = "product_sequence")
    private Long id;

    /**
     * The name (cannot be empty).
     */
    @NotEmpty
    @Size(max = 100)
    private String name;

    /**
     * The (optional) category.
     */
    @ManyToOne(cascade = CascadeType.ALL)
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

    public Long getId() {
        return this.id;
    }

    public void setId(final Long id) {
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
