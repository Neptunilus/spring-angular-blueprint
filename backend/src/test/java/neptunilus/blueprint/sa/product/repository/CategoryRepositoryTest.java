package neptunilus.blueprint.sa.product.repository;

import neptunilus.blueprint.sa.product.model.Category;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import javax.validation.ConstraintViolationException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests for {@link CategoryRepository}.
 */
@SpringJUnitConfig
@DataJpaTest
public class CategoryRepositoryIntegrationTest {

    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    public void testFindOneByName_ShouldFind() {
        Category categoryToFind = new Category("myCategory");
        this.testEntityManager.persist(categoryToFind);

        Category categoryNotToFind = new Category("myOtherCategory");
        this.testEntityManager.persist(categoryNotToFind);

        this.testEntityManager.flush();
        this.testEntityManager.clear();

        Optional<Category> category = this.categoryRepository.findOneByName("myCategory");
        assertThat(category).isPresent();
        assertThat(category).get().extracting("name").isEqualTo("myCategory");
    }

    @Test
    public void testFindOneByName_ShouldNotFind() {
        Optional<Category> category = this.categoryRepository.findOneByName("myCategory");
        assertThat(category).isNotPresent();
    }

    @Test
    public void testFindByNameContainingIgnoreCase_ShouldFindTheCorrectOnes() {
        Category categoryToFind1 = new Category("myCategory1");
        this.testEntityManager.persist(categoryToFind1);

        Category categoryToFind2 = new Category("myCategory2");
        this.testEntityManager.persist(categoryToFind2);

        Category categoryNotToFind = new Category("SomethingElse");
        this.testEntityManager.persist(categoryNotToFind);

        this.testEntityManager.flush();
        this.testEntityManager.clear();

        Page<Category> categories = this.categoryRepository.findByNameContainingIgnoreCase("category", null);
        assertThat(categories).hasSize(2);
        assertThat(categories).extracting("name").containsExactlyInAnyOrder("myCategory1", "myCategory2");
    }

    @Test
    public void testInsertInvalid_ShouldThrowException() {
        Category invalidCategory = new Category("");

        assertThatThrownBy(() -> this.testEntityManager.persistAndFlush(invalidCategory))
                .isInstanceOf(ConstraintViolationException.class)
                .extracting("constraintViolations").asInstanceOf(InstanceOfAssertFactories.ITERABLE)
                .extracting("propertyPath").asString().contains("name");
    }
}
