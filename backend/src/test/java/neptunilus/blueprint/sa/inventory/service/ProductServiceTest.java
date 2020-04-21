package neptunilus.blueprint.sa.inventory.service;

import neptunilus.blueprint.sa.inventory.exception.ProductAlreadyExistsException;
import neptunilus.blueprint.sa.inventory.exception.ProductNotFoundException;
import neptunilus.blueprint.sa.inventory.model.Category;
import neptunilus.blueprint.sa.inventory.model.Product;
import neptunilus.blueprint.sa.inventory.repository.ProductRepository;
import neptunilus.blueprint.sa.inventory.service.impl.ProductServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

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
    public void testFind_shouldFindSomeWithoutSearchAndWithoutStrictAndWithoutCategory() {
        String search = null;
        boolean strict = false;
        UUID categoryId = null;
        Pageable pageable = Pageable.unpaged();

        Product existingProduct = new Product("myProduct");
        doReturn(new PageImpl<>(Collections.singletonList(existingProduct))).when(this.productRepository).findAll(pageable);

        Page<Product> page = this.productService.find(search, strict, categoryId, pageable);

        assertThat(page).hasSize(1);
        assertThat(page).extracting("name").containsExactly("myProduct");

        verify(this.productRepository).findAll(pageable);
        verifyNoMoreInteractions(this.productRepository, this.categoryService);
    }

    @Test
    public void testFind_shouldFindSomeWithoutSearchAndWithoutStrictAndWithCategory() {
        String search = null;
        boolean strict = false;
        Pageable pageable = Pageable.unpaged();

        UUID existingCategoryId = UUID.randomUUID();
        Category category = new Category("myCategory");
        category.setId(existingCategoryId);

        Category existingCategory = new Category("myCategory");
        doReturn(existingCategory).when(this.categoryService).get(existingCategoryId);

        Product existingProduct = new Product("myProduct");
        doReturn(new PageImpl<>(Collections.singletonList(existingProduct))).when(this.productRepository).findByCategory(existingCategory, pageable);

        Page<Product> page = this.productService.find(search, strict, existingCategoryId, pageable);

        assertThat(page).hasSize(1);
        assertThat(page).extracting("name").containsExactly("myProduct");

        verify(this.categoryService).get(existingCategoryId);
        verify(this.productRepository).findByCategory(existingCategory, pageable);
        verifyNoMoreInteractions(this.productRepository, this.categoryService);
    }

    @Test
    public void testFind_shouldFindSomeWithoutSearchAndWithStrictAndWithoutCategory() {
        String search = null;
        boolean strict = true;
        UUID categoryId = null;
        Pageable pageable = Pageable.unpaged();

        Product existingProduct = new Product("myProduct");
        doReturn(new PageImpl<>(Collections.singletonList(existingProduct))).when(this.productRepository).findAll(pageable);

        Page<Product> page = this.productService.find(search, strict, categoryId, pageable);

        assertThat(page).hasSize(1);
        assertThat(page).extracting("name").containsExactly("myProduct");

        verify(this.productRepository).findAll(pageable);
        verifyNoMoreInteractions(this.productRepository, this.categoryService);
    }

    @Test
    public void testFind_shouldFindSomeWithoutSearchAndWithStrictAndWithCategory() {
        String search = null;
        boolean strict = true;
        Pageable pageable = Pageable.unpaged();

        UUID existingCategoryId = UUID.randomUUID();
        Category category = new Category("myCategory");
        category.setId(existingCategoryId);

        Category existingCategory = new Category("myCategory");
        doReturn(existingCategory).when(this.categoryService).get(existingCategoryId);

        Product existingProduct = new Product("myProduct");
        doReturn(new PageImpl<>(Collections.singletonList(existingProduct))).when(this.productRepository).findByCategory(existingCategory, pageable);

        Page<Product> page = this.productService.find(search, strict, existingCategoryId, pageable);

        assertThat(page).hasSize(1);
        assertThat(page).extracting("name").containsExactly("myProduct");

        verify(this.categoryService).get(existingCategoryId);
        verify(this.productRepository).findByCategory(existingCategory, pageable);
        verifyNoMoreInteractions(this.productRepository, this.categoryService);
    }

    @Test
    public void testFind_shouldFindSomeWithSearchAndWithoutStrictAndWithoutCategory() {
        String search = "search";
        boolean strict = false;
        UUID categoryId = null;
        Pageable pageable = Pageable.unpaged();

        Product existingProduct = new Product("myProduct");
        doReturn(new PageImpl<>(Collections.singletonList(existingProduct))).when(this.productRepository).findByNameContainingIgnoreCase(search, pageable);

        Page<Product> page = this.productService.find(search, strict, categoryId, pageable);

        assertThat(page).hasSize(1);
        assertThat(page).extracting("name").containsExactly("myProduct");

        verify(this.productRepository).findByNameContainingIgnoreCase(search, pageable);
        verifyNoMoreInteractions(this.productRepository, this.categoryService);
    }

    @Test
    public void testFind_shouldFindSomeWithSearchAndWithoutStrictAndWithCategory() {
        String search = "search";
        boolean strict = false;
        Pageable pageable = Pageable.unpaged();

        UUID existingCategoryId = UUID.randomUUID();
        Category category = new Category("myCategory");
        category.setId(existingCategoryId);

        Category existingCategory = new Category("myCategory");
        doReturn(existingCategory).when(this.categoryService).get(existingCategoryId);

        Product existingProduct = new Product("myProduct");
        doReturn(new PageImpl<>(Collections.singletonList(existingProduct))).when(this.productRepository)
                .findByNameContainingIgnoreCaseAndCategory(search, existingCategory, pageable);

        Page<Product> page = this.productService.find(search, strict, existingCategoryId, pageable);

        assertThat(page).hasSize(1);
        assertThat(page).extracting("name").containsExactly("myProduct");

        verify(this.categoryService).get(existingCategoryId);
        verify(this.productRepository).findByNameContainingIgnoreCaseAndCategory(search, existingCategory, pageable);
        verifyNoMoreInteractions(this.productRepository, this.categoryService);
    }

    @Test
    public void testFind_shouldFindSomeWithSearchAndWithStrictAndWithoutCategory() {
        String search = "search";
        boolean strict = true;
        UUID categoryId = null;
        Pageable pageable = Pageable.unpaged();

        Product existingProduct = new Product("myProduct");
        doReturn(Optional.of(existingProduct)).when(this.productRepository).findOneByName(search);

        Page<Product> page = this.productService.find(search, strict, categoryId, pageable);

        assertThat(page).hasSize(1);
        assertThat(page).extracting("name").containsExactly("myProduct");

        verify(this.productRepository).findOneByName(search);
        verifyNoMoreInteractions(this.productRepository, this.categoryService);
    }

    @Test
    public void testFind_shouldFindSomeWithSearchAndWithStrictAndWithCategory() {
        String search = "search";
        boolean strict = true;
        Pageable pageable = Pageable.unpaged();

        UUID existingCategoryId = UUID.randomUUID();
        Category category = new Category("myCategory");
        category.setId(existingCategoryId);

        Category existingCategory = new Category("myCategory");
        doReturn(existingCategory).when(this.categoryService).get(existingCategoryId);

        Product existingProduct = new Product("myProduct");
        doReturn(Optional.of(existingProduct)).when(this.productRepository).findOneByNameAndCategory(search, existingCategory);

        Page<Product> page = this.productService.find(search, strict, existingCategoryId, pageable);

        assertThat(page).hasSize(1);
        assertThat(page).extracting("name").containsExactly("myProduct");

        verify(this.categoryService).get(existingCategoryId);
        verify(this.productRepository).findOneByNameAndCategory(search, existingCategory);
        verifyNoMoreInteractions(this.productRepository, this.categoryService);
    }

    @Test
    public void testGet_ShouldThrowExceptionIfIdNotProvided() {
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> this.productService.get(null))
                .withMessageContainingAll("id", "null");
        verifyNoInteractions(this.productRepository, this.categoryService);
    }

    @Test
    public void testGet_ShouldThrowExceptionIfProductNotFound() {
        UUID id = UUID.randomUUID();

        doReturn(Optional.empty()).when(this.productRepository).findById(id);

        assertThatExceptionOfType(ProductNotFoundException.class)
                .isThrownBy(() -> this.productService.get(id))
                .withMessageContainingAll("no", "product", id.toString());
        verify(this.productRepository).findById(id);
        verifyNoMoreInteractions(this.productRepository, this.categoryService);
    }

    @Test
    public void testGet_ShouldReturnProductIfFound() {
        UUID id = UUID.randomUUID();
        Product product = new Product("myProduct");

        doReturn(Optional.of(product)).when(this.productRepository).findById(id);

        Product productReturned = this.productService.get(id);
        assertThat(productReturned).isSameAs(product);

        verify(this.productRepository).findById(id);
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
        String name = "myProduct";
        Product existingProduct = new Product(name);
        Product newProduct = new Product(name);

        doReturn(Optional.of(existingProduct)).when(this.productRepository).findOneByName(name);

        assertThatExceptionOfType(ProductAlreadyExistsException.class)
                .isThrownBy(() -> this.productService.create(newProduct))
                .withMessageContainingAll("product", "exists", name);
        verify(this.productRepository).findOneByName(name);
        verifyNoMoreInteractions(this.productRepository, this.categoryService);
    }

    @Test
    public void testCreate_ShouldSaveNewProductIfNotAlreadyExistsWithCategory() {
        ArgumentCaptor<Product> productCaptor = ArgumentCaptor.forClass(Product.class);

        UUID categoryId = UUID.randomUUID();
        Category category = new Category("category");
        category.setId(categoryId);

        String name = "myProduct";
        Product newProduct = new Product(name, category);

        Category existingCategory = new Category("category");
        doReturn(existingCategory).when(this.categoryService).get(categoryId);

        doReturn(Optional.empty()).when(this.productRepository).findOneByName(name);

        Product persistedProduct = new Product(name, existingCategory);
        persistedProduct.setId(UUID.randomUUID());

        doReturn(persistedProduct).when(this.productRepository).save(any(Product.class));

        UUID newId = this.productService.create(newProduct);

        verify(this.productRepository).findOneByName(name);
        verify(this.categoryService).get(categoryId);
        verify(this.productRepository).save(productCaptor.capture());
        assertThat(newId).isEqualTo(persistedProduct.getId());
        assertThat(productCaptor.getValue()).extracting("name").isEqualTo(name);
        assertThat(productCaptor.getValue()).extracting("category").isSameAs(existingCategory);
        verifyNoMoreInteractions(this.productRepository, this.categoryService);
    }

    @Test
    public void testCreate_ShouldSaveNewProductIfNotAlreadyExistsWithoutCategory() {
        ArgumentCaptor<Product> productCaptor = ArgumentCaptor.forClass(Product.class);

        String name = "myProduct";
        Product newProduct = new Product(name, null);

        doReturn(Optional.empty()).when(this.productRepository).findOneByName(name);

        Product persistedProduct = new Product(name);
        persistedProduct.setId(UUID.randomUUID());

        doReturn(persistedProduct).when(this.productRepository).save(any(Product.class));

        UUID newId = this.productService.create(newProduct);

        verify(this.productRepository).findOneByName(name);
        verify(this.productRepository).save(productCaptor.capture());
        assertThat(newId).isEqualTo(persistedProduct.getId());
        assertThat(productCaptor.getValue()).extracting("name").isEqualTo(name);
        assertThat(productCaptor.getValue()).extracting("category").isNull();
        verifyNoMoreInteractions(this.productRepository, this.categoryService);
    }

    @Test
    public void testUpdate_ShouldThrowExceptionIfIdNotProvided() {
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> this.productService.update(null, new Product(null)))
                .withMessageContainingAll("id", "null");
        verifyNoInteractions(this.productRepository, this.categoryService);
    }

    @Test
    public void testUpdate_ShouldThrowExceptionIfUpdateNotProvided() {
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> this.productService.update(UUID.randomUUID(), null))
                .withMessageContainingAll("data", "null");
        verifyNoInteractions(this.productRepository, this.categoryService);
    }

    @Test
    public void testUpdate_ShouldThrowExceptionIfProductNotFound() {
        UUID id = UUID.randomUUID();

        doReturn(Optional.empty()).when(this.productRepository).findById(id);

        assertThatExceptionOfType(ProductNotFoundException.class)
                .isThrownBy(() -> this.productService.update(id, new Product(null)))
                .withMessageContainingAll("no", "product", id.toString());
        verify(this.productRepository).findById(id);
        verifyNoMoreInteractions(this.productRepository, this.categoryService);
    }

    @Test
    public void testUpdate_ShouldThrowExceptionIfNewProductNameAlreadyExists() {
        UUID id = UUID.randomUUID();
        String name = "myProduct";
        String newName = "myNewProduct";

        Product existingProduct = new Product(name);
        doReturn(Optional.of(existingProduct)).when(this.productRepository).findById(id);

        Product conflictingProduct = new Product(newName);
        doReturn(Optional.of(conflictingProduct)).when(this.productRepository).findOneByName(newName);

        Product update = new Product(newName);

        assertThatExceptionOfType(ProductAlreadyExistsException.class)
                .isThrownBy(() -> this.productService.update(id, update))
                .withMessageContainingAll("product", "exists", newName);
        verify(this.productRepository).findById(id);
        verify(this.productRepository).findOneByName(newName);
        verifyNoMoreInteractions(this.productRepository, this.categoryService);
    }

    @Test
    public void testUpdate_ShouldTriggerUpdateIfProductExistsWithCategory() {
        ArgumentCaptor<Product> productCaptor = ArgumentCaptor.forClass(Product.class);

        UUID id = UUID.randomUUID();
        String name = "myProduct";
        String newName = "newName";

        UUID newCategoryId = UUID.randomUUID();
        Category newCategory = new Category("category");
        newCategory.setId(newCategoryId);

        Product existingProduct = new Product(name);
        Product update = new Product(newName, newCategory);

        Category existingCategory = new Category("category");
        doReturn(existingCategory).when(this.categoryService).get(newCategoryId);

        doReturn(Optional.of(existingProduct)).when(this.productRepository).findById(id);

        doReturn(Optional.empty()).when(this.productRepository).findOneByName(newName);

        this.productService.update(id, update);

        verify(this.productRepository).findById(id);
        verify(this.categoryService).get(newCategoryId);
        verify(this.productRepository).findOneByName(newName);
        verify(this.productRepository).save(productCaptor.capture());
        assertThat(productCaptor.getValue()).isSameAs(existingProduct);
        assertThat(productCaptor.getValue()).extracting("name").isEqualTo(newName);
        assertThat(productCaptor.getValue()).extracting("category").isSameAs(existingCategory);
        verifyNoMoreInteractions(this.productRepository, this.categoryService);
    }

    @Test
    public void testUpdate_ShouldTriggerUpdateIfProductExistsWithoutCategory() {
        ArgumentCaptor<Product> productCaptor = ArgumentCaptor.forClass(Product.class);

        UUID id = UUID.randomUUID();
        String name = "myProduct";
        String newName = "newName";

        Product existingProduct = new Product(name);
        Product update = new Product(newName);

        doReturn(Optional.of(existingProduct)).when(this.productRepository).findById(id);

        doReturn(Optional.empty()).when(this.productRepository).findOneByName(newName);

        this.productService.update(id, update);

        verify(this.productRepository).findById(id);
        verify(this.productRepository).findOneByName(newName);
        verify(this.productRepository).save(productCaptor.capture());
        assertThat(productCaptor.getValue()).isSameAs(existingProduct);
        assertThat(productCaptor.getValue()).extracting("name").isEqualTo(newName);
        assertThat(productCaptor.getValue()).extracting("category").isNull();
        verifyNoMoreInteractions(this.productRepository, this.categoryService);
    }

    @Test
    public void testDelete_ShouldDoNothingIfIdNotProvided() {
        this.productService.delete(null);
        verifyNoInteractions(this.productRepository, this.categoryService);
    }

    @Test
    public void testDelete_ShouldDoNothingIfProductNotExists() {
        UUID id = UUID.randomUUID();

        doReturn(Optional.empty()).when(this.productRepository).findById(id);

        this.productService.delete(id);

        verify(this.productRepository).findById(id);
        verifyNoMoreInteractions(this.productRepository, this.categoryService);
    }

    @Test
    public void testDelete_ShouldDeleteProductIfExists() {
        UUID id = UUID.randomUUID();
        Product existingProduct = new Product("myProduct");

        doReturn(Optional.of(existingProduct)).when(this.productRepository).findById(id);

        this.productService.delete(id);

        verify(this.productRepository).findById(id);
        verify(this.productRepository).delete(same(existingProduct));
        verifyNoMoreInteractions(this.productRepository, this.categoryService);
    }
}
