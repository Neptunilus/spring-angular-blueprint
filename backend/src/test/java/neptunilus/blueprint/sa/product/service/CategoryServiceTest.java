package neptunilus.blueprint.sa.product.service;

import neptunilus.blueprint.sa.product.exception.CategoryAlreadyExistsException;
import neptunilus.blueprint.sa.product.exception.CategoryNotFoundException;
import neptunilus.blueprint.sa.product.model.Category;
import neptunilus.blueprint.sa.product.repository.CategoryRepository;
import neptunilus.blueprint.sa.product.service.impl.CategoryServiceImpl;
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
public class CategoryServiceTest {

    private final CategoryRepository categoryRepository;
    private final CategoryService categoryService;

    CategoryServiceTest(@Mock final CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
        this.categoryService = new CategoryServiceImpl(categoryRepository);
    }

    @Test
    public void testFind_shouldFindSomeWithSearchAndWithStrict() {
        String search = "search";
        boolean strict = true;
        Pageable pageable = Pageable.unpaged();

        Category existingCategory = new Category("myCategory");
        doReturn(Optional.of(existingCategory)).when(this.categoryRepository).findOneByName(search);

        Page<Category> page = this.categoryService.find(search, strict, pageable);

        assertThat(page).hasSize(1);
        assertThat(page).extracting("name").containsExactly("myCategory");

        verify(this.categoryRepository).findOneByName(search);
        verifyNoMoreInteractions(this.categoryRepository);
    }

    @Test
    public void testFind_shouldFindSomeWithSearchAndWithoutStrict() {
        String search = "search";
        boolean strict = false;
        Pageable pageable = Pageable.unpaged();

        Category existingCategory = new Category("myCategory");
        doReturn(new PageImpl<>(Collections.singletonList(existingCategory))).when(this.categoryRepository).findByNameContainingIgnoreCase(search, pageable);

        Page<Category> page = this.categoryService.find(search, strict, pageable);

        assertThat(page).hasSize(1);
        assertThat(page).extracting("name").containsExactly("myCategory");

        verify(this.categoryRepository).findByNameContainingIgnoreCase(search, pageable);
        verifyNoMoreInteractions(this.categoryRepository);
    }

    @Test
    public void testFind_shouldFindAllWithoutSearchAndWithStrict() {
        String search = null;
        boolean strict = true;
        Pageable pageable = Pageable.unpaged();

        Category existingCategory = new Category("myCategory");
        doReturn(new PageImpl<>(Collections.singletonList(existingCategory))).when(this.categoryRepository).findAll(pageable);

        Page<Category> page = this.categoryService.find(search, strict, pageable);

        assertThat(page).hasSize(1);
        assertThat(page).extracting("name").containsExactly("myCategory");

        verify(this.categoryRepository).findAll(pageable);
        verifyNoMoreInteractions(this.categoryRepository);
    }

    @Test
    public void testFind_shouldFindAllWithoutSearchAndWithoutStrict() {
        String search = null;
        boolean strict = false;
        Pageable pageable = Pageable.unpaged();

        Category existingCategory = new Category("myCategory");
        doReturn(new PageImpl<>(Collections.singletonList(existingCategory))).when(this.categoryRepository).findAll(pageable);

        Page<Category> page = this.categoryService.find(search, strict, pageable);

        assertThat(page).hasSize(1);
        assertThat(page).extracting("name").containsExactly("myCategory");

        verify(this.categoryRepository).findAll(pageable);
        verifyNoMoreInteractions(this.categoryRepository);
    }

    @Test
    public void testGet_ShouldThrowExceptionIfIdNotProvided() {
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> this.categoryService.get(null))
                .withMessageContainingAll("id", "null");
        verifyNoInteractions(this.categoryRepository);
    }

    @Test
    public void testGet_ShouldThrowExceptionIfCategoryNotFound() {
        UUID id = UUID.randomUUID();

        doReturn(Optional.empty()).when(this.categoryRepository).findById(id);

        assertThatExceptionOfType(CategoryNotFoundException.class)
                .isThrownBy(() -> this.categoryService.get(id))
                .withMessageContainingAll("no", "category", id.toString());
        verify(this.categoryRepository).findById(id);
        verifyNoMoreInteractions(this.categoryRepository);
    }

    @Test
    public void testGet_ShouldReturnCategoryIfFound() {
        UUID id = UUID.randomUUID();
        Category category = new Category("myCategory");

        doReturn(Optional.of(category)).when(this.categoryRepository).findById(id);

        Category categoryReturned = this.categoryService.get(id);
        assertThat(categoryReturned).isSameAs(category);

        verify(this.categoryRepository).findById(id);
        verifyNoMoreInteractions(this.categoryRepository);
    }

    @Test
    public void testCreate_ShouldThrowExceptionIfCategoryNotProvided() {
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> this.categoryService.create(null))
                .withMessageContainingAll("category", "null");
        verifyNoInteractions(this.categoryRepository);
    }

    @Test
    public void testCreate_ShouldThrowExceptionIfCategoryAlreadyExists() {
        String name = "myCategory";
        Category existingCategory = new Category(name);
        Category newCategory = new Category(name);

        doReturn(Optional.of(existingCategory)).when(this.categoryRepository).findOneByName(name);

        assertThatExceptionOfType(CategoryAlreadyExistsException.class)
                .isThrownBy(() -> this.categoryService.create(newCategory))
                .withMessageContainingAll("category", "exists", name);
        verify(this.categoryRepository).findOneByName(name);
        verifyNoMoreInteractions(this.categoryRepository);
    }

    @Test
    public void testCreate_ShouldSaveNewCategoryIfNotAlreadyExists() {
        ArgumentCaptor<Category> categoryCaptor = ArgumentCaptor.forClass(Category.class);

        String name = "myCategory";
        Category newCategory = new Category(name);

        doReturn(Optional.empty()).when(this.categoryRepository).findOneByName(name);

        Category persistedCategory = new Category(name);
        persistedCategory.setId(UUID.randomUUID());
        doReturn(persistedCategory).when(this.categoryRepository).save(any(Category.class));

        UUID newId = this.categoryService.create(newCategory);

        verify(this.categoryRepository).findOneByName(name);
        verify(this.categoryRepository).save(categoryCaptor.capture());
        assertThat(newId).isEqualTo(persistedCategory.getId());
        assertThat(categoryCaptor.getValue()).extracting("name").isEqualTo(name);
        verifyNoMoreInteractions(this.categoryRepository);
    }

    @Test
    public void testUpdate_ShouldThrowExceptionIfIdNotProvided() {
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> this.categoryService.update(null, new Category(null)))
                .withMessageContainingAll("id", "null");
        verifyNoInteractions(this.categoryRepository);
    }

    @Test
    public void testUpdate_ShouldThrowExceptionIfUpdateNotProvided() {
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> this.categoryService.update(UUID.randomUUID(), null))
                .withMessageContainingAll("data", "null");
        verifyNoInteractions(this.categoryRepository);
    }

    @Test
    public void testUpdate_ShouldThrowExceptionIfCategoryNotFound() {
        UUID id = UUID.randomUUID();

        doReturn(Optional.empty()).when(this.categoryRepository).findById(id);

        assertThatExceptionOfType(CategoryNotFoundException.class)
                .isThrownBy(() -> this.categoryService.update(id, new Category(null)))
                .withMessageContainingAll("no", "category", id.toString());
        verify(this.categoryRepository).findById(id);
        verifyNoMoreInteractions(this.categoryRepository);
    }

    @Test
    public void testUpdate_ShouldThrowExceptionIfNewCategoryNameAlreadyExists() {
        UUID id = UUID.randomUUID();
        String name = "myCategory";
        String newName = "myNewCategory";

        Category existingCategory = new Category(name);
        doReturn(Optional.of(existingCategory)).when(this.categoryRepository).findById(id);

        Category conflictingCategory = new Category(newName);
        doReturn(Optional.of(conflictingCategory)).when(this.categoryRepository).findOneByName(newName);

        Category update = new Category(newName);

        assertThatExceptionOfType(CategoryAlreadyExistsException.class)
                .isThrownBy(() -> this.categoryService.update(id, update))
                .withMessageContainingAll("category", "exists", newName);
        verify(this.categoryRepository).findById(id);
        verify(this.categoryRepository).findOneByName(newName);
        verifyNoMoreInteractions(this.categoryRepository);
    }

    @Test
    public void testUpdate_ShouldTriggerUpdateIfCategoryExistsWithNewName() {
        ArgumentCaptor<Category> categoryCaptor = ArgumentCaptor.forClass(Category.class);

        UUID id = UUID.randomUUID();
        String name = "myCategory";
        String newName = "newName";
        Category existingCategory = new Category(name);
        Category update = new Category(newName);

        doReturn(Optional.of(existingCategory)).when(this.categoryRepository).findById(id);
        doReturn(Optional.empty()).when(this.categoryRepository).findOneByName(newName);

        this.categoryService.update(id, update);

        verify(this.categoryRepository).findById(id);
        verify(this.categoryRepository).findOneByName(newName);
        verify(this.categoryRepository).save(categoryCaptor.capture());
        assertThat(categoryCaptor.getValue()).isSameAs(existingCategory);
        assertThat(categoryCaptor.getValue()).extracting("name").isEqualTo(newName);
        verifyNoMoreInteractions(this.categoryRepository);
    }

    @Test
    public void testUpdate_ShouldTriggerUpdateIfCategoryExistsWithoutNewName() {
        ArgumentCaptor<Category> categoryCaptor = ArgumentCaptor.forClass(Category.class);

        UUID id = UUID.randomUUID();
        String name = "myCategory";
        Category existingCategory = new Category(name);
        Category update = new Category(name);

        doReturn(Optional.of(existingCategory)).when(this.categoryRepository).findById(id);

        this.categoryService.update(id, update);

        verify(this.categoryRepository).findById(id);
        verify(this.categoryRepository).save(categoryCaptor.capture());
        assertThat(categoryCaptor.getValue()).isSameAs(existingCategory);
        assertThat(categoryCaptor.getValue()).extracting("name").isEqualTo(name);
        verifyNoMoreInteractions(this.categoryRepository);
    }

    @Test
    public void testDelete_ShouldDoNothingIfIdNotProvided() {
        this.categoryService.delete(null);
        verifyNoInteractions(this.categoryRepository);
    }

    @Test
    public void testDelete_ShouldDoNothingIfCategoryNotExists() {
        UUID id = UUID.randomUUID();

        doReturn(Optional.empty()).when(this.categoryRepository).findById(id);

        this.categoryService.delete(id);

        verify(this.categoryRepository).findById(id);
        verifyNoMoreInteractions(this.categoryRepository);
    }

    @Test
    public void testDelete_ShouldDeleteCategoryIfExists() {
        UUID id = UUID.randomUUID();
        Category existingCategory = new Category("myCategory");

        doReturn(Optional.of(existingCategory)).when(this.categoryRepository).findById(id);

        this.categoryService.delete(id);

        verify(this.categoryRepository).findById(id);
        verify(this.categoryRepository).delete(same(existingCategory));
        verifyNoMoreInteractions(this.categoryRepository);
    }
}
