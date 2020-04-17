package neptunilus.blueprint.sa.security.repository;

import neptunilus.blueprint.sa.security.model.Authority;
import neptunilus.blueprint.sa.security.model.User;
import neptunilus.blueprint.sa.security.model.UserRole;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import javax.validation.ConstraintViolationException;
import java.util.Optional;
import java.util.Set;

import static neptunilus.blueprint.sa.security.model.Authority.CREATE_CATEGORY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests for {@link UserRepository}.
 */
@SpringJUnitConfig
@DataJpaTest
public class UserRepositoryTest {

    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testFindOneByEmail_ShouldFind() {
        UserRole userRole = new UserRole("admin", Set.of(CREATE_CATEGORY, Authority.CREATE_PRODUCT));
        this.testEntityManager.persist(userRole);
        User userToFind = new User("test@test.xy", "abc", userRole);
        this.testEntityManager.persist(userToFind);

        User userNotToFind = new User("test2@test.xy", "abc", userRole);
        this.testEntityManager.persist(userNotToFind);

        this.testEntityManager.flush();
        this.testEntityManager.clear();

        Optional<User> user = this.userRepository.findOneByEmail("test@test.xy");
        assertThat(user).isPresent();
        assertThat(user).get().extracting("email").isEqualTo("test@test.xy");
        assertThat(user).get().extracting("password").isEqualTo("abc");
        assertThat(user).get().extracting("role").extracting("name").isEqualTo("admin");
    }

    @Test
    public void testFindOneByEmail_ShouldNotFind() {
        Optional<User> user = this.userRepository.findOneByEmail("test@test.xy");
        assertThat(user).isNotPresent();
    }

    @Test
    public void testFindByEmailContainingIgnoreCase_ShouldFindTheCorrectOnes() {
        UserRole userRole = new UserRole("role", Set.of(CREATE_CATEGORY));
        this.testEntityManager.persist(userRole);

        User userToFind1 = new User("test@abc.xy", "password", userRole);
        this.testEntityManager.persist(userToFind1);

        User userToFind2 = new User("test2@abc.xy", "password", userRole);
        this.testEntityManager.persist(userToFind2);

        User userNotToFind = new User("test@xyz.ab", "password", userRole);
        this.testEntityManager.persist(userNotToFind);

        this.testEntityManager.flush();
        this.testEntityManager.clear();

        Page<User> users = this.userRepository.findByEmailContainingIgnoreCase("abc", null);
        assertThat(users).hasSize(2);
        assertThat(users).extracting("email").containsExactlyInAnyOrder("test@abc.xy", "test2@abc.xy");
    }

    @Test
    public void testInsertInvalid_ShouldThrowException() {
        User invalidUser = new User("abc", "abc", null);

        assertThatThrownBy(() -> this.testEntityManager.persistAndFlush(invalidUser))
                .isInstanceOf(ConstraintViolationException.class)
                .extracting("constraintViolations").asInstanceOf(InstanceOfAssertFactories.ITERABLE)
                .extracting("propertyPath").asString().contains("email", "role");
    }
}
