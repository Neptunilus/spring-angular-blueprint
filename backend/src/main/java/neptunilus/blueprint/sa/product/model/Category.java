package neptunilus.blueprint.sa.product.model;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

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
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "category_generator")
    @SequenceGenerator(name = "category_generator", sequenceName = "category_sequence")
    private Long id;

    /**
     * The (unique) name (cannot be empty).
     */
    @NotEmpty
    @Size(max = 50)
    @Column(unique = true)
    private String name;

    protected Category() {
        super();
    }

    public Category(final String name) {
        this.name = name;
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

}
