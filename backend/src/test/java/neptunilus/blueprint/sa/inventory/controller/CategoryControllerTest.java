package neptunilus.blueprint.sa.inventory.controller;

import neptunilus.blueprint.sa.inventory.controller.in.CategoryCreateRequest;
import neptunilus.blueprint.sa.inventory.controller.in.CategoryUpdateRequest;
import neptunilus.blueprint.sa.inventory.controller.out.CategoryResponse;
import neptunilus.blueprint.sa.inventory.exception.CategoryAlreadyExistsException;
import neptunilus.blueprint.sa.inventory.exception.CategoryNotFoundException;
import neptunilus.blueprint.sa.inventory.model.Category;
import neptunilus.blueprint.sa.inventory.service.CategoryService;
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

@WebMvcTest(CategoryController.class)
@WithMockUser
@AutoConfigureMockMvc(addFilters = false) // TODO remove
public class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CategoryService categoryService;

    @MockBean
    @Qualifier("inventoryMapper")
    private ModelMapper modelMapper;

    @Test
    public void testSearch_ShouldReturnEmptyPageIfNothingFound() throws Exception {
        String search = "search";

        doReturn(Page.empty()).when(this.categoryService).find(eq(search), eq(false), any(Pageable.class));

        this.mockMvc
                .perform(
                        get("/category")
                                .param("search", search)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty());

        verify(this.categoryService).find(eq(search), eq(false), any(Pageable.class));

        verifyNoMoreInteractions(this.categoryService, this.modelMapper);
    }

    @Test
    public void testSearch_ShouldReturnCategoriesIfFoundWithoutSearch() throws Exception {
        Category category = new Category("myCategory");
        Page<Category> categories = new PageImpl<>(List.of(category));
        doReturn(categories).when(this.categoryService).find(isNull(), eq(false), any(Pageable.class));

        CategoryResponse categoryResponse = new CategoryResponse();
        categoryResponse.setId(UUID.randomUUID());
        categoryResponse.setName("myCategory");
        doReturn(categoryResponse).when(this.modelMapper).map(category, CategoryResponse.class);

        this.mockMvc
                .perform(
                        get("/category")
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].id").value(equalTo(categoryResponse.getId().toString())))
                .andExpect(jsonPath("$.content[0].name").value(equalTo(categoryResponse.getName())));

        verify(this.categoryService).find(isNull(), eq(false), any(Pageable.class));
        verify(this.modelMapper).map(category, CategoryResponse.class);

        verifyNoMoreInteractions(this.categoryService, this.modelMapper);
    }

    @Test
    public void testSearch_ShouldReturnCategoriesIfFoundWithSearchAndWithPagination() throws Exception {
        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        String search = "search";

        Category category = new Category("myCategory");
        Page<Category> categories = new PageImpl<>(List.of(category));
        doReturn(categories).when(this.categoryService).find(eq(search), eq(false), any(Pageable.class));

        CategoryResponse categoryResponse = new CategoryResponse();
        categoryResponse.setId(UUID.randomUUID());
        categoryResponse.setName("myCategory");
        doReturn(categoryResponse).when(this.modelMapper).map(category, CategoryResponse.class);

        this.mockMvc
                .perform(
                        get("/category")
                                .param("search", search)
                                .param("page", "2")
                                .param("size", "10")
                                .param("sort", "name,desc")
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].id").value(equalTo(categoryResponse.getId().toString())))
                .andExpect(jsonPath("$.content[0].name").value(equalTo(categoryResponse.getName())));

        verify(this.categoryService).find(eq(search), eq(false), pageableCaptor.capture());
        assertThat(pageableCaptor.getValue()).extracting("pageNumber").isEqualTo(2);
        assertThat(pageableCaptor.getValue()).extracting("pageSize").isEqualTo(10);
        assertThat(pageableCaptor.getValue().getSort().getOrderFor("name")).extracting("direction").isEqualTo(Sort.Direction.DESC);
        verify(this.modelMapper).map(category, CategoryResponse.class);

        verifyNoMoreInteractions(this.categoryService, this.modelMapper);
    }

    @Test
    public void testGet_ShouldReturn404IfNotFound() throws Exception {
        UUID id = UUID.randomUUID();

        doThrow(new CategoryNotFoundException(String.format("no category found with id '%s'", id)))
                .when(this.categoryService).get(id);

        this.mockMvc
                .perform(
                        get("/category/{id}", id)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[0]").value(equalTo(String.format("no category found with id '%s'", id))));

        verify(this.categoryService).get(id);

        verifyNoMoreInteractions(this.categoryService, this.modelMapper);
    }

    @Test
    public void testGet_ShouldReturn400IfIdIsInvalid() throws Exception {
        String id = "testId";

        this.mockMvc
                .perform(
                        get("/category/{id}", id)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[0]").value(containsStringIgnoringCase("invalid")))
                .andExpect(jsonPath("$.errors[0]").value(containsStringIgnoringCase(id)));

        verifyNoInteractions(this.categoryService, this.modelMapper);
    }

    @Test
    public void testGet_ShouldReturnCategoryIfEverythingIsFine() throws Exception {
        UUID id = UUID.randomUUID();

        Category category = new Category("myCategory");
        category.setId(id);
        doReturn(category).when(this.categoryService).get(id);

        CategoryResponse categoryResponse = new CategoryResponse();
        categoryResponse.setId(id);
        categoryResponse.setName("myCategory");
        doReturn(categoryResponse).when(this.modelMapper).map(category, CategoryResponse.class);

        this.mockMvc
                .perform(
                        get("/category/{id}", id)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(equalTo(categoryResponse.getId().toString())))
                .andExpect(jsonPath("$.name").value(equalTo(categoryResponse.getName())));

        verify(this.categoryService).get(id);
        verify(this.modelMapper).map(category, CategoryResponse.class);

        verifyNoMoreInteractions(this.categoryService, this.modelMapper);
    }

    @Test
    public void testCreate_ShouldReturn409IfCategoryAlreadyExists() throws Exception {
        ArgumentCaptor<CategoryCreateRequest> categoryRequestCaptor = ArgumentCaptor.forClass(CategoryCreateRequest.class);

        String body = "{ \"name\": \"myCategory\" }";

        Category category = new Category("myCategory");
        doReturn(category).when(this.modelMapper).map(any(CategoryCreateRequest.class), eq(Category.class));

        doThrow(new CategoryAlreadyExistsException(String.format("category with name '%s' already exists", "myCategory")))
                .when(this.categoryService).create(category);

        this.mockMvc
                .perform(
                        post("/category")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(body)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[0]").value(containsStringIgnoringCase("exists")))
                .andExpect(jsonPath("$.errors[0]").value(containsStringIgnoringCase("myCategory")));

        verify(this.modelMapper).map(categoryRequestCaptor.capture(), eq(Category.class));
        assertThat(categoryRequestCaptor.getValue()).extracting("name").isEqualTo("myCategory");
        verify(this.categoryService).create(category);

        verifyNoMoreInteractions(this.categoryService, this.modelMapper);
    }

    @Test
    public void testCreate_ShouldReturn400IfDataIsInvalid() throws Exception {
        String body = "{ \"name\": \"\" }";

        this.mockMvc
                .perform(
                        post("/category")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(body)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[0]").value(equalTo("category name must not be empty")));

        verifyNoInteractions(this.categoryService, this.modelMapper);
    }

    @Test
    public void testCreate_ShouldReturn400IfDataIsInvalidInPersistence() throws Exception {
        String body = "{ \"name\": \"myCategory\" }";

        Category category = new Category("myCategory");
        doReturn(category).when(this.modelMapper).map(any(CategoryCreateRequest.class), eq(Category.class));

        doThrow(new DataIntegrityViolationException("invalid"))
                .when(this.categoryService).create(category);

        this.mockMvc
                .perform(
                        post("/category")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(body)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[0]").value(equalTo("invalid")));

        verify(this.modelMapper).map(any(CategoryCreateRequest.class), eq(Category.class));
        verify(this.categoryService).create(category);

        verifyNoMoreInteractions(this.categoryService, this.modelMapper);
    }

    @Test
    public void testCreate_ShouldCreateIfEverythingIsFine() throws Exception {
        ArgumentCaptor<CategoryCreateRequest> categoryRequestCaptor = ArgumentCaptor.forClass(CategoryCreateRequest.class);

        String body = "{ \"name\": \"myCategory\" }";

        Category category = new Category("myCategory");
        doReturn(category).when(this.modelMapper).map(any(CategoryCreateRequest.class), eq(Category.class));

        UUID newId = UUID.randomUUID();
        doReturn(newId).when(this.categoryService).create(category);

        this.mockMvc
                .perform(
                        post("/category")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(body)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString(newId.toString())))
                .andExpect(jsonPath("$").doesNotExist());

        verify(this.modelMapper).map(categoryRequestCaptor.capture(), eq(Category.class));
        assertThat(categoryRequestCaptor.getValue()).extracting("name").isEqualTo("myCategory");
        verify(this.categoryService).create(category);

        verifyNoMoreInteractions(this.categoryService, this.modelMapper);
    }

    @Test
    public void testUpdate_ShouldReturn404IfCategoryNotFound() throws Exception {
        ArgumentCaptor<CategoryUpdateRequest> categoryRequestCaptor = ArgumentCaptor.forClass(CategoryUpdateRequest.class);

        UUID id = UUID.randomUUID();
        String update = "{ \"name\": \"myCategory\" }";

        Category category = new Category("myCategory");
        doReturn(category).when(this.modelMapper).map(any(CategoryUpdateRequest.class), eq(Category.class));

        doThrow(new CategoryNotFoundException(String.format("no category found with id '%s'", id)))
                .when(this.categoryService).update(id, category);

        this.mockMvc
                .perform(
                        put("/category/{id}", id)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(update)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[0]").value(equalTo(String.format("no category found with id '%s'", id))));

        verify(this.modelMapper).map(categoryRequestCaptor.capture(), eq(Category.class));
        assertThat(categoryRequestCaptor.getValue()).extracting("name").isEqualTo("myCategory");
        verify(this.categoryService).update(id, category);

        verifyNoMoreInteractions(this.categoryService, this.modelMapper);
    }

    @Test
    public void testUpdate_ShouldReturn409IfNewNameAlreadyExists() throws Exception {
        ArgumentCaptor<CategoryUpdateRequest> categoryRequestCaptor = ArgumentCaptor.forClass(CategoryUpdateRequest.class);

        UUID id = UUID.randomUUID();
        String update = "{ \"name\": \"myCategory\" }";

        Category category = new Category("myCategory");
        doReturn(category).when(this.modelMapper).map(any(CategoryUpdateRequest.class), eq(Category.class));

        doThrow(new CategoryAlreadyExistsException(String.format("category with name '%s' already exists", "myCategory")))
                .when(this.categoryService).update(id, category);

        this.mockMvc
                .perform(
                        put("/category/{id}", id)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(update)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[0]").value(equalTo(String.format("category with name '%s' already exists", "myCategory"))));

        verify(this.modelMapper).map(categoryRequestCaptor.capture(), eq(Category.class));
        assertThat(categoryRequestCaptor.getValue()).extracting("name").isEqualTo("myCategory");
        verify(this.categoryService).update(id, category);

        verifyNoMoreInteractions(this.categoryService, this.modelMapper);
    }

    @Test
    public void testUpdate_ShouldReturn400IfIdIsInvalid() throws Exception {
        String id = "testId";
        String update = "{ \"name\": \"myCategory\" }";

        this.mockMvc
                .perform(
                        put("/category/{id}", id)
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

        verifyNoInteractions(this.categoryService, this.modelMapper);
    }

    @Test
    public void testUpdate_ShouldReturn400IfDataIsInvalid() throws Exception {
        UUID id = UUID.randomUUID();
        String update = "{ \"name\": \"\" }";

        this.mockMvc
                .perform(
                        put("/category/{id}", id)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(update)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[0]").value(containsStringIgnoringCase("category name must not be empty")));

        verifyNoInteractions(this.categoryService, this.modelMapper);
    }

    @Test
    public void testUpdate_ShouldReturn400IfDataIsInvalidInPersistence() throws Exception {
        UUID id = UUID.randomUUID();
        String body = "{ \"name\": \"myCategory\" }";

        Category category = new Category("myCategory");
        doReturn(category).when(this.modelMapper).map(any(CategoryUpdateRequest.class), eq(Category.class));

        doThrow(new DataIntegrityViolationException("invalid"))
                .when(this.categoryService).update(id, category);

        this.mockMvc
                .perform(
                        put("/category/{id}", id)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(body)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[0]").value(equalTo("invalid")));

        verify(this.modelMapper).map(any(CategoryUpdateRequest.class), eq(Category.class));
        verify(this.categoryService).update(id, category);

        verifyNoMoreInteractions(this.categoryService, this.modelMapper);
    }

    @Test
    public void testUpdate_ShouldUpdateIfEverythingIsFine() throws Exception {
        ArgumentCaptor<CategoryUpdateRequest> categoryRequestCaptor = ArgumentCaptor.forClass(CategoryUpdateRequest.class);

        UUID id = UUID.randomUUID();
        String update = "{ \"name\": \"myCategory\" }";

        Category category = new Category("myCategory");
        doReturn(category).when(this.modelMapper).map(any(CategoryUpdateRequest.class), eq(Category.class));

        doNothing().when(this.categoryService).update(id, category);

        this.mockMvc
                .perform(
                        put("/category/{id}", id)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(update)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$").doesNotExist());

        verify(this.modelMapper).map(categoryRequestCaptor.capture(), eq(Category.class));
        assertThat(categoryRequestCaptor.getValue()).extracting("name").isEqualTo("myCategory");
        verify(this.categoryService).update(id, category);

        verifyNoMoreInteractions(this.categoryService, this.modelMapper);
    }

    @Test
    public void testDelete_ShouldDelete() throws Exception {
        UUID id = UUID.randomUUID();

        this.mockMvc
                .perform(
                        delete("/category/{id}", id)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$").doesNotExist());

        verify(this.categoryService).delete(id);

        verifyNoMoreInteractions(this.categoryService, this.modelMapper);
    }
}
