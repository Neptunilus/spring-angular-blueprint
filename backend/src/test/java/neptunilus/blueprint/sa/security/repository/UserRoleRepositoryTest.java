package neptunilus.blueprint.sa.security.repository;

import neptunilus.blueprint.sa.security.model.UserRole;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import javax.validation.ConstraintViolationException;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests for {@link UserRepository}.
 */
@SpringJUnitConfig
@DataJpaTest
public class UserRoleRepositoryIntegrationTest {

    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private UserRoleRepository userRoleRepository;

    @Test
    public void testInsertInvalid_ShouldThrowException() {
        UserRole invalidUserRole = new UserRole("", null);

        assertThatThrownBy(() -> this.testEntityManager.persistAndFlush(invalidUserRole))
                .isInstanceOf(ConstraintViolationException.class)
                .extracting("constraintViolations").asInstanceOf(InstanceOfAssertFactories.ITERABLE)
                .extracting("propertyPath").asString().contains("name", "authorities");
    }
}
