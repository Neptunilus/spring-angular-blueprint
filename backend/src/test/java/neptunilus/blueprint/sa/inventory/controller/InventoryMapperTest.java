package neptunilus.blueprint.sa.inventory.controller;

import neptunilus.blueprint.sa.common.configuration.MappingConfiguration;
import neptunilus.blueprint.sa.inventory.controller.in.*;
import neptunilus.blueprint.sa.inventory.controller.out.CategoryResponse;
import neptunilus.blueprint.sa.inventory.controller.out.ProductResponse;
import neptunilus.blueprint.sa.inventory.model.Category;
import neptunilus.blueprint.sa.inventory.model.Product;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class InventoryMapperTest {

    private final ModelMapper inventoryMapper = new MappingConfiguration().modelMapper();

    @Test
    public void testMap_ShouldMapCategoryCreateRequestToCategoryCorrectly() {
        String categoryName = "categoryName";

        CategoryCreateRequest categoryRequest = new CategoryCreateRequest();
        categoryRequest.setName(categoryName);

        Category category = this.inventoryMapper.map(categoryRequest, Category.class);

        assertThat(category).extracting("id").isNull();
        assertThat(category).extracting("name").isEqualTo(categoryName);
    }

    @Test
    public void testMap_ShouldMapCategoryUpdateRequestToCategoryCorrectly() {
        String categoryName = "categoryName";

        CategoryUpdateRequest categoryRequest = new CategoryUpdateRequest();
        categoryRequest.setName(categoryName);

        Category category = this.inventoryMapper.map(categoryRequest, Category.class);

        assertThat(category).extracting("id").isNull();
        assertThat(category).extracting("name").isEqualTo(categoryName);
    }

    @Test
    public void testMap_ShouldMapCategoryToCategoryResponseCorrectly() {
        UUID categoryId = UUID.randomUUID();
        String categoryName = "categoryName";

        Category category = new Category(categoryName);
        category.setId(categoryId);

        CategoryResponse categoryResponse = this.inventoryMapper.map(category, CategoryResponse.class);

        assertThat(categoryResponse).extracting("id").isEqualTo(categoryId);
        assertThat(categoryResponse).extracting("name").isEqualTo(categoryName);
    }

    @Test
    public void testMap_ShouldMapProductCreateRequestToProductCorrectly() {
        UUID categoryId = UUID.randomUUID();

        String productName = "productName";

        CategoryReferenceRequest categoryRequest = new CategoryReferenceRequest();
        categoryRequest.setId(categoryId);

        ProductCreateRequest productRequest = new ProductCreateRequest();
        productRequest.setName(productName);
        productRequest.setCategory(categoryRequest);

        Product product = this.inventoryMapper.map(productRequest, Product.class);

        assertThat(product).extracting("id").isNull();
        assertThat(product).extracting("name").isEqualTo(productName);
        assertThat(product).extracting("category").extracting("id").isEqualTo(categoryId);
        assertThat(product).extracting("category").extracting("name").isNull();
    }

    @Test
    public void testMap_ShouldMapProductUpdateRequestToProductCorrectly() {
        UUID categoryId = UUID.randomUUID();

        String productName = "productName";

        CategoryReferenceRequest categoryRequest = new CategoryReferenceRequest();
        categoryRequest.setId(categoryId);

        ProductUpdateRequest productRequest = new ProductUpdateRequest();
        productRequest.setName(productName);
        productRequest.setCategory(categoryRequest);

        Product product = this.inventoryMapper.map(productRequest, Product.class);

        assertThat(product).extracting("id").isNull();
        assertThat(product).extracting("name").isEqualTo(productName);
        assertThat(product).extracting("category").extracting("id").isEqualTo(categoryId);
        assertThat(product).extracting("category").extracting("name").isNull();
    }

    @Test
    public void testMap_ShouldMapProductToProductResponseCorrectly() {
        UUID categoryId = UUID.randomUUID();
        String categoryName = "categoryName";
        UUID productId = UUID.randomUUID();
        String productName = "productName";

        Category category = new Category(categoryName);
        category.setId(categoryId);

        Product product = new Product(productName, category);
        product.setId(productId);

        ProductResponse productResponse = this.inventoryMapper.map(product, ProductResponse.class);

        assertThat(productResponse).extracting("id").isEqualTo(productId);
        assertThat(productResponse).extracting("name").isEqualTo(productName);
        assertThat(productResponse).extracting("category").extracting("id").isEqualTo(categoryId);
        assertThat(productResponse).extracting("category").extracting("name").isEqualTo(categoryName);
    }
}
