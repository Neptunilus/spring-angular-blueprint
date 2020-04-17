package neptunilus.blueprint.sa.security.repository;

import neptunilus.blueprint.sa.security.model.UserRole;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import javax.validation.ConstraintViolationException;
import java.util.Optional;
import java.util.Set;

import static neptunilus.blueprint.sa.security.model.Authority.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests for {@link UserRepository}.
 */
@SpringJUnitConfig
@DataJpaTest
public class UserRoleRepositoryTest {

    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private UserRoleRepository userRoleRepository;

    @Test
    public void testFindOneByName_ShouldFind() {
        UserRole userRoleToFind = new UserRole("myRole", Set.of(UPDATE_PRODUCT, CREATE_USER));
        this.testEntityManager.persist(userRoleToFind);

        UserRole userRoleNotToFind = new UserRole("otherRole", Set.of(CREATE_CATEGORY, UPDATE_USER));
        this.testEntityManager.persist(userRoleNotToFind);

        this.testEntityManager.flush();
        this.testEntityManager.clear();

        Optional<UserRole> userRole = this.userRoleRepository.findOneByName("myRole");
        assertThat(userRole).isPresent();
        assertThat(userRole).get().extracting("name").isEqualTo("myRole");
        assertThat(userRole).get().extracting("authorities").asInstanceOf(InstanceOfAssertFactories.ITERABLE).containsExactlyInAnyOrder(UPDATE_PRODUCT, CREATE_USER);
    }

    @Test
    public void testFindOneByName_ShouldNotFind() {
        Optional<UserRole> userRole = this.userRoleRepository.findOneByName("myRole");
        assertThat(userRole).isNotPresent();
    }

    @Test
    public void testInsertInvalid_ShouldThrowException() {
        UserRole invalidUserRole = new UserRole("", null);

        assertThatThrownBy(() -> this.testEntityManager.persistAndFlush(invalidUserRole))
                .isInstanceOf(ConstraintViolationException.class)
                .extracting("constraintViolations").asInstanceOf(InstanceOfAssertFactories.ITERABLE)
                .extracting("propertyPath").asString().contains("name", "authorities");
    }
}
