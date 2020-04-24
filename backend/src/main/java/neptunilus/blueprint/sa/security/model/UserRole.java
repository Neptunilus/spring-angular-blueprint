package neptunilus.blueprint.sa.security.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static lombok.AccessLevel.PROTECTED;

/**
 * A user role.
 */
@Getter
@Setter
@NoArgsConstructor(access = PROTECTED)
@Entity
@Table(name = "user_role")
public class UserRole {

    /**
     * The unique identifier.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    /**
     * The name (not empty).
     */
    @NotEmpty
    @Size(max = 50)
    @Column(unique = true)
    private String name;

    /**
     * The assigned authorities (not empty).
     */
    @NotEmpty
    @ElementCollection
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "user_role_authority", joinColumns = @JoinColumn(name = "user_role_id"))
    @Column(name = "authority")
    private Set<Authority> authorities = new HashSet<>();

    public UserRole(final String name, final Set<Authority> authorities) {
        this.name = name;
        if (authorities != null) {
            this.authorities.addAll(authorities);
        }
    }

}
