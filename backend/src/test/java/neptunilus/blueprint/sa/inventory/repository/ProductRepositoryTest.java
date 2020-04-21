package neptunilus.blueprint.sa.inventory.repository;

import neptunilus.blueprint.sa.inventory.model.Category;
import neptunilus.blueprint.sa.inventory.model.Product;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import javax.validation.ConstraintViolationException;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringJUnitConfig
@DataJpaTest
public class ProductRepositoryTest {

    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private ProductRepository productRepository;

    @Test
    public void testFindById_ShouldFind() {
        Category category = new Category("category");
        this.testEntityManager.persist(category);

        Product productToFind = new Product("myProduct", category);
        UUID idToFind = this.testEntityManager.persist(productToFind).getId();

        Product productNotToFind = new Product("myOtherProduct", category);
        this.testEntityManager.persist(productNotToFind);

        this.testEntityManager.flush();
        this.testEntityManager.clear();

        Optional<Product> product = this.productRepository.findById(idToFind);
        assertThat(product).isPresent();
        assertThat(product).get().extracting("name").isEqualTo("myProduct");
        assertThat(product.get().getCategory()).isNotNull();
        assertThat(product.get().getCategory()).extracting("name").isEqualTo("category");
    }

    @Test
    public void testFindById_ShouldNotFind() {
        Optional<Product> product = this.productRepository.findById(UUID.randomUUID());
        assertThat(product).isNotPresent();
    }

    @Test
    public void testFindOneByName_ShouldFind() {
        Category category = new Category("category");
        this.testEntityManager.persist(category);

        Product productToFind = new Product("myProduct", category);
        this.testEntityManager.persist(productToFind);

        Product productNotToFind = new Product("myOtherProduct", category);
        this.testEntityManager.persist(productNotToFind);

        this.testEntityManager.flush();
        this.testEntityManager.clear();

        Optional<Product> product = this.productRepository.findOneByName("myProduct");
        assertThat(product).isPresent();
        assertThat(product).get().extracting("name").isEqualTo("myProduct");
        assertThat(product.get().getCategory()).isNotNull();
        assertThat(product.get().getCategory()).extracting("name").isEqualTo("category");
    }

    @Test
    public void testFindOneByName_ShouldNotFind() {
        Optional<Product> product = this.productRepository.findOneByName("myProduct");
        assertThat(product).isNotPresent();
    }

    @Test
    public void testFindOneByNameAndCategory_ShouldFind() {
        Category categoryToFind = new Category("category");
        this.testEntityManager.persist(categoryToFind);

        Product productToFind = new Product("myProduct", categoryToFind);
        this.testEntityManager.persist(productToFind);

        Category categoryNotToFind = new Category("otherCategory");
        this.testEntityManager.persist(categoryNotToFind);

        Product productNotToFind = new Product("myOtherProduct", categoryNotToFind);
        this.testEntityManager.persist(productNotToFind);

        this.testEntityManager.flush();
        this.testEntityManager.clear();

        Optional<Product> product = this.productRepository.findOneByNameAndCategory("myProduct", categoryToFind);
        assertThat(product).isPresent();
        assertThat(product).get().extracting("name").isEqualTo("myProduct");
        assertThat(product.get().getCategory()).isNotNull();
        assertThat(product.get().getCategory()).extracting("name").isEqualTo("category");
    }

    @Test
    public void testFindOneByNameAndCategory_ShouldNotFind() {
        Category category = new Category("category");
        this.testEntityManager.persist(category);

        Product product = new Product("myProduct", category);
        this.testEntityManager.persist(product);

        Optional<Product> productFound = this.productRepository.findOneByNameAndCategory("myProduct2", category);
        assertThat(productFound).isNotPresent();
    }

    @Test
    public void testFindByNameContainingIgnoreCase_ShouldFindTheCorrectOnes() {
        Category category = new Category("category");
        this.testEntityManager.persist(category);

        Product productToFind1 = new Product("myProduct1", category);
        this.testEntityManager.persist(productToFind1);

        Product productToFind2 = new Product("myProduct2");
        this.testEntityManager.persist(productToFind2);

        Product productNotToFind = new Product("SomethingElse", category);
        this.testEntityManager.persist(productNotToFind);

        this.testEntityManager.flush();
        this.testEntityManager.clear();

        Page<Product> products = this.productRepository.findByNameContainingIgnoreCase("product", null);
        assertThat(products).hasSize(2);
        assertThat(products).extracting("name").containsExactlyInAnyOrder("myProduct1", "myProduct2");
    }

    @Test
    public void testFindByCategory_ShouldFind() {
        Category categoryToFind = new Category("category1");
        this.testEntityManager.persist(categoryToFind);
        Product productToFind = new Product("myProduct1", categoryToFind);
        this.testEntityManager.persist(productToFind);

        Category categoryNotToFind = new Category("category2");
        this.testEntityManager.persist(categoryNotToFind);
        Product productNotToFind = new Product("myProduct2", categoryNotToFind);
        this.testEntityManager.persist(productNotToFind);

        this.testEntityManager.flush();
        this.testEntityManager.clear();

        Page<Product> products = this.productRepository.findByCategory(categoryToFind, null);
        assertThat(products).hasSize(1);
        assertThat(products).extracting("name").containsExactly("myProduct1");
        assertThat(products).extracting("category").extracting("name").containsExactly("category1");
    }

    @Test
    public void testFindByCategory_ShouldNotFind() {
        Category categoryWithoutProduct = new Category("category1");
        this.testEntityManager.persist(categoryWithoutProduct);

        Category categoryNotToFind = new Category("category2");
        this.testEntityManager.persist(categoryNotToFind);
        Product productNotToFind = new Product("myProduct2", categoryNotToFind);
        this.testEntityManager.persist(productNotToFind);

        this.testEntityManager.flush();
        this.testEntityManager.clear();

        Page<Product> products = this.productRepository.findByCategory(categoryWithoutProduct, null);
        assertThat(products).isEmpty();
    }

    @Test
    public void testFindByNameContainingIgnoreCaseAndCategory_ShouldFindTheCorrectOnes() {
        Category category = new Category("category");
        this.testEntityManager.persist(category);

        Product productToFind = new Product("myProduct1", category);
        this.testEntityManager.persist(productToFind);

        Product productNotToFind1 = new Product("myProduct2");
        this.testEntityManager.persist(productNotToFind1);

        Product productNotToFind2 = new Product("SomethingElse", category);
        this.testEntityManager.persist(productNotToFind2);

        this.testEntityManager.flush();
        this.testEntityManager.clear();

        Page<Product> products = this.productRepository.findByNameContainingIgnoreCaseAndCategory("product", category, null);
        assertThat(products).hasSize(1);
        assertThat(products).extracting("name").containsExactly("myProduct1");
    }

    @Test
    public void testInsertInvalid_ShouldThrowException() {
        Product invalidProduct = new Product("");

        assertThatThrownBy(() -> this.testEntityManager.persistAndFlush(invalidProduct))
                .isInstanceOf(ConstraintViolationException.class)
                .extracting("constraintViolations").asInstanceOf(InstanceOfAssertFactories.ITERABLE)
                .extracting("propertyPath").asString().contains("name");
    }
}
