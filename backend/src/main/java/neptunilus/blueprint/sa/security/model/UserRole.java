package neptunilus.blueprint.sa.security.model;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

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

    protected UserRole() {
        super();
    }

    public UserRole(final String name, final Set<Authority> authorities) {
        this.name = name;
        if (authorities != null) {
            this.authorities.addAll(authorities);
        }
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
