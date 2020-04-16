package neptunilus.blueprint.sa.security.model;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * A user.
 */
@Entity
@Table(name = "user")
public class User {

    /**
     * The unique identifier.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_generator")
    @SequenceGenerator(name = "user_generator", sequenceName = "user_sequence")
    private Long id;

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
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "role_id")
    private UserRole role;

    protected User() {
        super();
    }

    public User(final String email, final String password, final UserRole role) {
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(final String email) {
        this.email = email;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(final String password) {
        this.password = password;
    }

    public UserRole getRole() {
        return this.role;
    }

    public void setRole(final UserRole role) {
        this.role = role;
    }
}
