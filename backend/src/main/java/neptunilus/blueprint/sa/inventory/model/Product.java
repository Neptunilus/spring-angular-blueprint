package neptunilus.blueprint.sa.inventory.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.UUID;

import static lombok.AccessLevel.PROTECTED;

/**
 * A product.
 */
@Getter @Setter
@NoArgsConstructor(access = PROTECTED)
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

    public Product(final String name) {
        this(name, null);
    }

    public Product(final String name, final Category category) {
        this.name = name;
        this.category = category;
    }

}
