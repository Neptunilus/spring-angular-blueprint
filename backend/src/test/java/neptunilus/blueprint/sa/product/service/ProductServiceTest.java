package neptunilus.blueprint.sa.product.service;

import neptunilus.blueprint.sa.product.exception.ProductAlreadyExistsException;
import neptunilus.blueprint.sa.product.exception.ProductNotFoundException;
import neptunilus.blueprint.sa.product.model.Category;
import neptunilus.blueprint.sa.product.model.Product;
import neptunilus.blueprint.sa.product.repository.ProductRepository;
import neptunilus.blueprint.sa.product.service.impl.ProductServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    private final ProductRepository productRepository;
    private final CategoryService categoryService;
    private final ProductService productService;

    ProductServiceTest(@Mock final ProductRepository productRepository, @Mock final CategoryService categoryService) {
        this.productRepository = productRepository;
        this.categoryService = categoryService;
        this.productService = new ProductServiceImpl(this.productRepository, this.categoryService);
    }

    @Test
    public void testFind_shouldFindSomeWithSearch() {
        final String search = "search";
        final Pageable pageable = Pageable.unpaged();

        this.productService.find(search, null, pageable);

        verify(this.productRepository).findByNameContainingIgnoreCase(search, pageable);
        verifyNoMoreInteractions(this.productRepository, this.categoryService);
    }

    @Test
    public void testFind_shouldFindSomeWithCategory() {
        final Category category = new Category("category");
        final Pageable pageable = Pageable.unpaged();

        final Category existingCategory = new Category("category");
        doReturn(existingCategory).when(this.categoryService).get(category.getName());

        this.productService.find(null, category, pageable);

        verify(this.categoryService).get(category.getName());
        verify(this.productRepository).findByCategory(same(existingCategory), same(pageable));
        verifyNoMoreInteractions(this.productRepository, this.categoryService);
    }

    @Test
    public void testFind_shouldFindSomeWithSearchAndCategory() {
        final String search = "search";
        final Category category = new Category("category");
        final Pageable pageable = Pageable.unpaged();

        final Category existingCategory = new Category("category");
        doReturn(existingCategory).when(this.categoryService).get(category.getName());

        this.productService.find(search, category, pageable);

        verify(this.categoryService).get(category.getName());
        verify(this.productRepository).findByNameContainingIgnoreCaseAndCategory(eq(search), same(existingCategory), same(pageable));
        verifyNoMoreInteractions(this.productRepository, this.categoryService);
    }

    @Test
    public void testFind_shouldFindAllWithoutSearchAndCategory() {
        final Pageable pageable = Pageable.unpaged();

        this.productService.find(null, null, pageable);

        verify(this.productRepository).findAll(same(pageable));
        verifyNoMoreInteractions(this.productRepository, this.categoryService);
    }

    @Test
    public void testGet_ShouldThrowExceptionIfNameNotProvided() {
        assertThatExceptionOfType(ProductNotFoundException.class)
                .isThrownBy(() -> this.productService.get(null))
                .withMessageContainingAll("empty", "name");
        verifyNoInteractions(this.productRepository, this.categoryService);
    }

    @Test
    public void testGet_ShouldThrowExceptionIfProductNotFound() {
        final String name = "myProduct";

        doReturn(Optional.empty()).when(this.productRepository).findOneByName(name);

        assertThatExceptionOfType(ProductNotFoundException.class)
                .isThrownBy(() -> this.productService.get(name))
                .withMessageContainingAll("no", "product", name);
        verify(this.productRepository).findOneByName(name);
        verifyNoMoreInteractions(this.productRepository, this.categoryService);
    }

    @Test
    public void testGet_ShouldReturnProductIfFound() {
        final String name = "myProduct";
        final Product product = new Product(name);

        doReturn(Optional.of(product)).when(this.productRepository).findOneByName(name);

        Product productReturned = this.productService.get(name);
        assertThat(productReturned).isSameAs(product);

        verify(this.productRepository).findOneByName(name);
        verifyNoMoreInteractions(this.productRepository, this.categoryService);
    }

    @Test
    public void testCreate_ShouldThrowExceptionIfProductNotProvided() {
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> this.productService.create(null))
                .withMessageContainingAll("product", "null");
        verifyNoInteractions(this.productRepository, this.categoryService);
    }

    @Test
    public void testCreate_ShouldThrowExceptionIfProductAlreadyExists() {
        final String name = "myProduct";
        final Product existingProduct = new Product(name);
        final Product newProduct = new Product(name);

        doReturn(Optional.of(existingProduct)).when(this.productRepository).findOneByName(name);

        assertThatExceptionOfType(ProductAlreadyExistsException.class)
                .isThrownBy(() -> this.productService.create(newProduct))
                .withMessageContainingAll("product", "exists", name);
        verify(this.productRepository).findOneByName(name);
        verifyNoMoreInteractions(this.productRepository, this.categoryService);
    }

    @Test
    public void testCreate_ShouldSaveNewProductIfNotAlreadyExistsWithCategory() {
        final ArgumentCaptor<Product> productCaptor = ArgumentCaptor.forClass(Product.class);

        final String name = "myProduct";
        final Category category = new Category("category");
        final Product newProduct = new Product(name, category);

        final Category existingCategory = new Category("category");
        doReturn(existingCategory).when(this.categoryService).get(category.getName());

        doReturn(Optional.empty()).when(this.productRepository).findOneByName(name);

        this.productService.create(newProduct);

        verify(this.productRepository).findOneByName(name);
        verify(this.categoryService).get(category.getName());
        verify(this.productRepository).save(productCaptor.capture());
        assertThat(productCaptor.getValue()).extracting("name").isEqualTo(name);
        assertThat(productCaptor.getValue()).extracting("category").isSameAs(existingCategory);
        verifyNoMoreInteractions(this.productRepository, this.categoryService);
    }

    @Test
    public void testCreate_ShouldSaveNewProductIfNotAlreadyExistsWithoutCategory() {
        final ArgumentCaptor<Product> productCaptor = ArgumentCaptor.forClass(Product.class);

        final String name = "myProduct";
        final Product newProduct = new Product(name, null);

        doReturn(Optional.empty()).when(this.productRepository).findOneByName(name);

        this.productService.create(newProduct);

        verify(this.productRepository).findOneByName(name);
        verify(this.productRepository).save(productCaptor.capture());
        assertThat(productCaptor.getValue()).extracting("name").isEqualTo(name);
        assertThat(productCaptor.getValue()).extracting("category").isNull();
        verifyNoMoreInteractions(this.productRepository, this.categoryService);
    }

    @Test
    public void testUpdate_ShouldThrowExceptionIfUpdateNotProvided() {
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> this.productService.update("product", null))
                .withMessageContainingAll("data", "null");
        verifyNoInteractions(this.productRepository, this.categoryService);
    }

    @Test
    public void testUpdate_ShouldThrowExceptionIfNameNotProvided() {
        assertThatExceptionOfType(ProductNotFoundException.class)
                .isThrownBy(() -> this.productService.update("", new Product(null)))
                .withMessageContainingAll("name", "empty");
        verifyNoInteractions(this.productRepository, this.categoryService);
    }

    @Test
    public void testUpdate_ShouldThrowExceptionIfProductNotFound() {
        final String name = "myProduct";

        doReturn(Optional.empty()).when(this.productRepository).findOneByName(name);

        assertThatExceptionOfType(ProductNotFoundException.class)
                .isThrownBy(() -> this.productService.update(name, new Product(null)))
                .withMessageContainingAll("no", "product", name);
        verify(this.productRepository).findOneByName(name);
        verifyNoMoreInteractions(this.productRepository, this.categoryService);
    }

    @Test
    public void testUpdate_ShouldTriggerUpdateIfProductExistsWithCategory() {
        final ArgumentCaptor<Product> productCaptor = ArgumentCaptor.forClass(Product.class);

        final String name = "myProduct";
        final String newName = "newName";
        final Category newCategory = new Category("category");
        final Product existingProduct = new Product(name);
        final Product update = new Product(newName, newCategory);

        final Category existingCategory = new Category("category");
        doReturn(existingCategory).when(this.categoryService).get(newCategory.getName());

        doReturn(Optional.of(existingProduct)).when(this.productRepository).findOneByName(name);

        this.productService.update(name, update);

        verify(this.productRepository).findOneByName(name);
        verify(this.categoryService).get(newCategory.getName());
        verify(this.productRepository).save(productCaptor.capture());
        assertThat(productCaptor.getValue()).isSameAs(existingProduct);
        assertThat(productCaptor.getValue()).extracting("name").isEqualTo(newName);
        assertThat(productCaptor.getValue()).extracting("category").isSameAs(existingCategory);
        verifyNoMoreInteractions(this.productRepository, this.categoryService);
    }

    @Test
    public void testUpdate_ShouldTriggerUpdateIfProductExistsWithoutCategory() {
        final ArgumentCaptor<Product> productCaptor = ArgumentCaptor.forClass(Product.class);

        final String name = "myProduct";
        final String newName = "newName";
        final Product existingProduct = new Product(name);
        final Product update = new Product(newName, null);

        doReturn(Optional.of(existingProduct)).when(this.productRepository).findOneByName(name);

        this.productService.update(name, update);

        verify(this.productRepository).findOneByName(name);
        verify(this.productRepository).save(productCaptor.capture());
        assertThat(productCaptor.getValue()).isSameAs(existingProduct);
        assertThat(productCaptor.getValue()).extracting("name").isEqualTo(newName);
        assertThat(productCaptor.getValue()).extracting("category").isNull();
        verifyNoMoreInteractions(this.productRepository, this.categoryService);
    }

    @Test
    public void testDelete_ShouldDoNothingIfNameNotProvided() {
        this.productService.delete(null);
        verifyNoInteractions(this.productRepository, this.categoryService);
    }

    @Test
    public void testDelete_ShouldDoNothingIfProductNotExists() {
        final String name = "myProduct";

        doReturn(Optional.empty()).when(this.productRepository).findOneByName(name);

        this.productService.delete(name);

        verify(this.productRepository).findOneByName(name);
        verifyNoMoreInteractions(this.productRepository, this.categoryService);
    }

    @Test
    public void testDelete_ShouldDeleteProductIfExists() {
        final String name = "myProduct";
        final Product existingProduct = new Product(name);

        doReturn(Optional.of(existingProduct)).when(this.productRepository).findOneByName(name);

        this.productService.delete(name);

        verify(this.productRepository).findOneByName(name);
        verify(this.productRepository).delete(same(existingProduct));
        verifyNoMoreInteractions(this.productRepository, this.categoryService);
    }
}
