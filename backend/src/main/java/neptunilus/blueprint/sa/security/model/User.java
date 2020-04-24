package neptunilus.blueprint.sa.security.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.UUID;

import static lombok.AccessLevel.PROTECTED;

/**
 * A user.
 */
@Getter @Setter
@NoArgsConstructor(access = PROTECTED)
@Entity
@Table(name = "user")
public class User {

    /**
     * The unique identifier.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    /**
     * The unique email address (not empty).
     */
    @NotEmpty
    @Email
    @Size(max = 100)
    @Column(unique = true)
    private String email;

    /**
     * The password (not empty).
     */
    @NotEmpty
    @Size(max = 200)
    private String password;

    /**
     * The role of the user.
     */
    @NotNull
    @ManyToOne
    @JoinColumn(name = "role_id")
    private UserRole role;

    public User(final String email, final String password, final UserRole role) {
        this.email = email;
        this.password = password;
        this.role = role;
    }

}
