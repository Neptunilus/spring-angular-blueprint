package neptunilus.blueprint.sa.inventory.controller;

import neptunilus.blueprint.sa.inventory.controller.in.ProductCreateRequest;
import neptunilus.blueprint.sa.inventory.controller.in.ProductUpdateRequest;
import neptunilus.blueprint.sa.inventory.controller.out.CategoryResponse;
import neptunilus.blueprint.sa.inventory.controller.out.ProductResponse;
import neptunilus.blueprint.sa.inventory.exception.CategoryNotFoundException;
import neptunilus.blueprint.sa.inventory.exception.ProductAlreadyExistsException;
import neptunilus.blueprint.sa.inventory.exception.ProductNotFoundException;
import neptunilus.blueprint.sa.inventory.model.Category;
import neptunilus.blueprint.sa.inventory.model.Product;
import neptunilus.blueprint.sa.inventory.service.ProductService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
@WithMockUser
@AutoConfigureMockMvc(addFilters = false) // TODO remove
public class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @MockBean
    @Qualifier("inventoryMapper")
    private ModelMapper modelMapper;

    @Test
    public void testSearch_ShouldReturnEmptyPageIfNothingFound() throws Exception {
        String search = "search";

        doReturn(Page.empty()).when(this.productService).find(eq(search), eq(false), isNull(), any(Pageable.class));

        this.mockMvc
                .perform(
                        get("/product")
                                .param("search", search)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty());

        verify(this.productService).find(eq(search), eq(false), isNull(), any(Pageable.class));

        verifyNoMoreInteractions(this.productService, this.modelMapper);
    }

    @Test
    public void testSearch_ShouldReturnProductsIfFoundWithoutCategoryAndWithoutSearch() throws Exception {
        Product product = new Product("myProduct", new Category("myCategory"));
        Page<Product> products = new PageImpl<>(List.of(product));
        doReturn(products).when(this.productService).find(isNull(), eq(false), isNull(), any(Pageable.class));

        CategoryResponse categoryResponse = new CategoryResponse();
        categoryResponse.setId(UUID.randomUUID());
        categoryResponse.setName("myCategory");

        ProductResponse productResponse = new ProductResponse();
        productResponse.setId(UUID.randomUUID());
        productResponse.setName("myProduct");
        productResponse.setCategory(categoryResponse);

        doReturn(productResponse).when(this.modelMapper).map(product, ProductResponse.class);

        this.mockMvc
                .perform(
                        get("/product")
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].id").value(equalTo(productResponse.getId().toString())))
                .andExpect(jsonPath("$.content[0].name").value(equalTo(productResponse.getName())))
                .andExpect(jsonPath("$.content[0].category.id").value(equalTo(categoryResponse.getId().toString())))
                .andExpect(jsonPath("$.content[0].category.name").value(equalTo(categoryResponse.getName())));

        verify(this.productService).find(isNull(), eq(false), isNull(), any(Pageable.class));
        verify(this.modelMapper).map(product, ProductResponse.class);

        verifyNoMoreInteractions(this.productService, this.modelMapper);
    }

    @Test
    public void testSearch_ShouldReturnProductsIfFoundWithCategoryAndWithSearchAndWithPagination() throws Exception {
        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        String search = "search";
        UUID categoryId = UUID.randomUUID();

        Product product = new Product("myProduct", new Category("myCategory"));
        Page<Product> products = new PageImpl<>(List.of(product));
        doReturn(products).when(this.productService).find(eq(search), eq(false), eq(categoryId), any(Pageable.class));

        CategoryResponse categoryResponse = new CategoryResponse();
        categoryResponse.setId(categoryId);
        categoryResponse.setName("myCategory");

        ProductResponse productResponse = new ProductResponse();
        productResponse.setId(UUID.randomUUID());
        productResponse.setName("myProduct");
        productResponse.setCategory(categoryResponse);

        doReturn(productResponse).when(this.modelMapper).map(product, ProductResponse.class);

        this.mockMvc
                .perform(
                        get("/product")
                                .param("search", search)
                                .param("categoryId", categoryId.toString())
                                .param("page", "2")
                                .param("size", "10")
                                .param("sort", "name,desc")
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].id").value(equalTo(productResponse.getId().toString())))
                .andExpect(jsonPath("$.content[0].name").value(equalTo(productResponse.getName())))
                .andExpect(jsonPath("$.content[0].category.id").value(equalTo(categoryResponse.getId().toString())))
                .andExpect(jsonPath("$.content[0].category.name").value(equalTo(categoryResponse.getName())));

        verify(this.productService).find(eq(search), eq(false), eq(categoryId), pageableCaptor.capture());
        assertThat(pageableCaptor.getValue()).extracting("pageNumber").isEqualTo(2);
        assertThat(pageableCaptor.getValue()).extracting("pageSize").isEqualTo(10);
        assertThat(pageableCaptor.getValue().getSort().getOrderFor("name")).extracting("direction").isEqualTo(Sort.Direction.DESC);
        verify(this.modelMapper).map(product, ProductResponse.class);

        verifyNoMoreInteractions(this.productService, this.modelMapper);
    }

    @Test
    public void testGet_ShouldReturn404IfNotFound() throws Exception {
        UUID id = UUID.randomUUID();

        doThrow(new ProductNotFoundException(String.format("no product found with id '%s'", id)))
                .when(this.productService).get(id);

        this.mockMvc
                .perform(
                        get("/product/{id}", id)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[0]").value(equalTo(String.format("no product found with id '%s'", id))));

        verify(this.productService).get(id);

        verifyNoMoreInteractions(this.productService, this.modelMapper);
    }

    @Test
    public void testGet_ShouldReturn400IfIdIsInvalid() throws Exception {
        String id = "testId";

        this.mockMvc
                .perform(
                        get("/product/{id}", id)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[0]").value(containsStringIgnoringCase("invalid")))
                .andExpect(jsonPath("$.errors[0]").value(containsStringIgnoringCase(id)));

        verifyNoInteractions(this.productService, this.modelMapper);
    }

    @Test
    public void testGet_ShouldReturnProductIfEverythingIsFine() throws Exception {
        UUID id = UUID.randomUUID();

        Product product = new Product("myProduct");
        doReturn(product).when(this.productService).get(id);

        ProductResponse productResponse = new ProductResponse();
        productResponse.setId(id);
        productResponse.setName("myProduct");
        doReturn(productResponse).when(this.modelMapper).map(product, ProductResponse.class);

        this.mockMvc
                .perform(
                        get("/product/{id}", id)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(equalTo(productResponse.getId().toString())))
                .andExpect(jsonPath("$.name").value(equalTo(productResponse.getName())))
                .andExpect(jsonPath("$.category").doesNotExist());

        verify(this.productService).get(id);
        verify(this.modelMapper).map(product, ProductResponse.class);

        verifyNoMoreInteractions(this.productService, this.modelMapper);
    }

    @Test
    public void testCreate_ShouldReturn409IfProductAlreadyExists() throws Exception {
        ArgumentCaptor<ProductCreateRequest> productRequestCaptor = ArgumentCaptor.forClass(ProductCreateRequest.class);

        String body = "{ \"name\": \"myProduct\" }";

        Product product = new Product("myProduct");
        doReturn(product).when(this.modelMapper).map(any(ProductCreateRequest.class), eq(Product.class));

        doThrow(new ProductAlreadyExistsException(String.format("product with name '%s' already exists", "myProduct")))
                .when(this.productService).create(product);

        this.mockMvc
                .perform(
                        post("/product")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(body)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[0]").value(containsStringIgnoringCase("exists")))
                .andExpect(jsonPath("$.errors[0]").value(containsStringIgnoringCase("myProduct")));

        verify(this.modelMapper).map(productRequestCaptor.capture(), eq(Product.class));
        assertThat(productRequestCaptor.getValue()).extracting("name").isEqualTo("myProduct");
        assertThat(productRequestCaptor.getValue()).extracting("category").isNull();
        verify(this.productService).create(product);

        verifyNoMoreInteractions(this.productService, this.modelMapper);
    }

    @Test
    public void testCreate_ShouldReturn404IfCategoryNotFound() throws Exception {
        ArgumentCaptor<ProductCreateRequest> productRequestCaptor = ArgumentCaptor.forClass(ProductCreateRequest.class);

        UUID categoryId = UUID.randomUUID();
        String body = "{ \"name\": \"myProduct\", \"category\": { \"id\": \"" + categoryId + "\" } }";

        Product product = new Product("myProduct");
        doReturn(product).when(this.modelMapper).map(any(ProductCreateRequest.class), eq(Product.class));

        doThrow(new CategoryNotFoundException(String.format("no category found with id '%s'", categoryId)))
                .when(this.productService).create(product);

        this.mockMvc
                .perform(
                        post("/product")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(body)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[0]").value(equalTo(String.format("no category found with id '%s'", categoryId))));

        verify(this.modelMapper).map(productRequestCaptor.capture(), eq(Product.class));
        assertThat(productRequestCaptor.getValue()).extracting("name").isEqualTo("myProduct");
        assertThat(productRequestCaptor.getValue()).extracting("category").extracting("id").isEqualTo(categoryId);
        verify(this.productService).create(product);

        verifyNoMoreInteractions(this.productService, this.modelMapper);
    }

    @Test
    public void testCreate_ShouldReturn400IfDataIsInvalid() throws Exception {
        String body = "{ \"name\": \"\" }";

        this.mockMvc
                .perform(
                        post("/product")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(body)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[0]").value(containsStringIgnoringCase("product name must not be empty")));

        verifyNoInteractions(this.productService, this.modelMapper);
    }

    @Test
    public void testCreate_ShouldReturn400IfDataIsInvalidInPersistence() throws Exception {
        String body = "{ \"name\": \"myProduct\" }";

        Product product = new Product("myProduct");
        doReturn(product).when(this.modelMapper).map(any(ProductCreateRequest.class), eq(Product.class));

        doThrow(new DataIntegrityViolationException("invalid"))
                .when(this.productService).create(product);

        this.mockMvc
                .perform(
                        post("/product")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(body)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[0]").value(equalTo("invalid")));

        verify(this.modelMapper).map(any(ProductCreateRequest.class), eq(Product.class));
        verify(this.productService).create(product);

        verifyNoMoreInteractions(this.productService, this.modelMapper);
    }

    @Test
    public void testCreate_ShouldCreateIfEverythingIsFine() throws Exception {
        ArgumentCaptor<ProductCreateRequest> productRequestCaptor = ArgumentCaptor.forClass(ProductCreateRequest.class);

        String body = "{ \"name\": \"myProduct\" }";

        Product product = new Product("myProduct");
        doReturn(product).when(this.modelMapper).map(any(ProductCreateRequest.class), eq(Product.class));

        UUID newId = UUID.randomUUID();
        doReturn(newId).when(this.productService).create(product);

        this.mockMvc
                .perform(
                        post("/product")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(body)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString(newId.toString())))
                .andExpect(jsonPath("$").doesNotExist());

        verify(this.modelMapper).map(productRequestCaptor.capture(), eq(Product.class));
        assertThat(productRequestCaptor.getValue()).extracting("name").isEqualTo("myProduct");
        verify(this.productService).create(product);

        verifyNoMoreInteractions(this.productService, this.modelMapper);
    }

    @Test
    public void testUpdate_ShouldReturn404IfProductNotFound() throws Exception {
        ArgumentCaptor<ProductUpdateRequest> productRequestCaptor = ArgumentCaptor.forClass(ProductUpdateRequest.class);

        UUID id = UUID.randomUUID();
        String update = "{ \"name\": \"myProduct\" }";

        Product product = new Product("myProduct");
        doReturn(product).when(this.modelMapper).map(any(ProductUpdateRequest.class), eq(Product.class));

        doThrow(new ProductNotFoundException(String.format("no product found with id '%s'", id)))
                .when(this.productService).update(id, product);

        this.mockMvc
                .perform(
                        put("/product/{id}", id)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(update)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[0]").value(equalTo(String.format("no product found with id '%s'", id))));

        verify(this.modelMapper).map(productRequestCaptor.capture(), eq(Product.class));
        assertThat(productRequestCaptor.getValue()).extracting("name").isEqualTo("myProduct");
        verify(this.productService).update(id, product);

        verifyNoMoreInteractions(this.productService, this.modelMapper);
    }

    @Test
    public void testUpdate_ShouldReturn409IfNewNameAlreadyExists() throws Exception {
        ArgumentCaptor<ProductUpdateRequest> productRequestCaptor = ArgumentCaptor.forClass(ProductUpdateRequest.class);

        UUID id = UUID.randomUUID();
        String update = "{ \"name\": \"myProduct\" }";

        Product product = new Product("myProduct");
        doReturn(product).when(this.modelMapper).map(any(ProductUpdateRequest.class), eq(Product.class));

        doThrow(new ProductAlreadyExistsException(String.format("product with name '%s' already exists", "myProduct")))
                .when(this.productService).update(id, product);

        this.mockMvc
                .perform(
                        put("/product/{id}", id)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(update)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[0]").value(equalTo(String.format("product with name '%s' already exists", "myProduct"))));

        verify(this.modelMapper).map(productRequestCaptor.capture(), eq(Product.class));
        assertThat(productRequestCaptor.getValue()).extracting("name").isEqualTo("myProduct");
        verify(this.productService).update(id, product);

        verifyNoMoreInteractions(this.productService, this.modelMapper);
    }

    @Test
    public void testUpdate_ShouldReturn400IfIdIsInvalid() throws Exception {
        String id = "testId";
        String update = "{ \"name\": \"myProduct\" }";

        this.mockMvc
                .perform(
                        put("/product/{id}", id)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(update)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[0]").value(containsStringIgnoringCase("invalid")))
                .andExpect(jsonPath("$.errors[0]").value(containsStringIgnoringCase(id)));

        verifyNoInteractions(this.productService, this.modelMapper);
    }

    @Test
    public void testUpdate_ShouldReturn400IfDataIsInvalid() throws Exception {
        UUID id = UUID.randomUUID();
        String update = "{ \"name\": \"\" }";

        this.mockMvc
                .perform(
                        put("/product/{id}", id)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(update)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[0]").value(containsStringIgnoringCase("product name must not be empty")));

        verifyNoInteractions(this.productService, this.modelMapper);
    }

    @Test
    public void testUpdate_ShouldReturn400IfDataIsInvalidInPersistence() throws Exception {
        UUID id = UUID.randomUUID();
        String body = "{ \"name\": \"myProduct\" }";

        Product product = new Product("myProduct");
        doReturn(product).when(this.modelMapper).map(any(ProductUpdateRequest.class), eq(Product.class));

        doThrow(new DataIntegrityViolationException("invalid"))
                .when(this.productService).update(id, product);

        this.mockMvc
                .perform(
                        put("/product/{id}", id)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(body)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[0]").value(equalTo("invalid")));

        verify(this.modelMapper).map(any(ProductUpdateRequest.class), eq(Product.class));
        verify(this.productService).update(id, product);

        verifyNoMoreInteractions(this.productService, this.modelMapper);
    }

    @Test
    public void testUpdate_ShouldReturn404IfCategoryNotFound() throws Exception {
        ArgumentCaptor<ProductUpdateRequest> productRequestCaptor = ArgumentCaptor.forClass(ProductUpdateRequest.class);

        UUID id = UUID.randomUUID();
        UUID categoryId = UUID.randomUUID();
        String update = "{ \"name\": \"myProduct\", \"category\": { \"id\": \"" + categoryId + "\" } }";

        Product product = new Product("myProduct");
        doReturn(product).when(this.modelMapper).map(any(ProductUpdateRequest.class), eq(Product.class));

        doThrow(new CategoryNotFoundException(String.format("no category found with id '%s'", categoryId)))
                .when(this.productService).update(id, product);

        this.mockMvc
                .perform(
                        put("/product/{id}", id)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(update)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[0]").value(equalTo(String.format("no category found with id '%s'", categoryId))));

        verify(this.modelMapper).map(productRequestCaptor.capture(), eq(Product.class));
        assertThat(productRequestCaptor.getValue()).extracting("name").isEqualTo("myProduct");
        assertThat(productRequestCaptor.getValue()).extracting("category").extracting("id").isEqualTo(categoryId);
        verify(this.productService).update(id, product);

        verifyNoMoreInteractions(this.productService, this.modelMapper);
    }

    @Test
    public void testUpdate_ShouldUpdateIfEverythingIsFine() throws Exception {
        ArgumentCaptor<ProductUpdateRequest> productRequestCaptor = ArgumentCaptor.forClass(ProductUpdateRequest.class);

        UUID id = UUID.randomUUID();
        String update = "{ \"name\": \"myProduct\" }";

        Product product = new Product("myProduct");
        doReturn(product).when(this.modelMapper).map(any(ProductUpdateRequest.class), eq(Product.class));

        doNothing().when(this.productService).update(id, product);

        this.mockMvc
                .perform(
                        put("/product/{id}", id)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(update)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$").doesNotExist());

        verify(this.modelMapper).map(productRequestCaptor.capture(), eq(Product.class));
        assertThat(productRequestCaptor.getValue()).extracting("name").isEqualTo("myProduct");
        verify(this.productService).update(id, product);

        verifyNoMoreInteractions(this.productService, this.modelMapper);
    }

    @Test
    public void testDelete_ShouldDelete() throws Exception {
        UUID id = UUID.randomUUID();

        this.mockMvc
                .perform(
                        delete("/product/{id}", id)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$").doesNotExist());

        verify(this.productService).delete(id);

        verifyNoMoreInteractions(this.productService, this.modelMapper);
    }
}
