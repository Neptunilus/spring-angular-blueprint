package neptunilus.blueprint.sa.inventory.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.UUID;

import static lombok.AccessLevel.PROTECTED;

/**
 * A category.
 */
@Getter @Setter
@NoArgsConstructor(access = PROTECTED)
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

    public Category(final String name) {
        this.name = name;
    }

}
