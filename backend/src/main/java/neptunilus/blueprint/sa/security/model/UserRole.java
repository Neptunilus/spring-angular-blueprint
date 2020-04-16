package neptunilus.blueprint.sa.security.model;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.util.HashSet;
import java.util.Set;

/**
 * A user role.
 */
@Entity
@Table(name = "user_role")
public class UserRole {

    /**
     * The unique identifier.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_role_generator")
    @SequenceGenerator(name = "user_role_generator", sequenceName = "user_role_sequence")
    private Long id;

    /**
     * The name (not empty).
     */
    @NotEmpty
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

    protected UserRole() {
        super();
    }

    public UserRole(final String name, final Set<Authority> authorities) {
        this.name = name;
        if (authorities != null) {
            this.authorities.addAll(authorities);
        }
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

    public Set<Authority> getAuthorities() {
        return this.authorities;
    }

    public void setAuthorities(final Set<Authority> authorities) {
        this.authorities = authorities;
    }

    public void addAuthority(final Authority authority) {
        this.authorities.add(authority);
    }

    public void removeAuthority(final Authority authority) {
        this.authorities.remove(authority);
    }
}
