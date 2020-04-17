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
import org.springframework.data.domain.Pageable;

import java.util.Optional;

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
    public void testFind_shouldFindSomeWithSearch() {
        final String search = "search";
        final Pageable pageable = Pageable.unpaged();

        this.categoryService.find(search, pageable);

        verify(this.categoryRepository).findByNameContainingIgnoreCase(search, pageable);
        verifyNoMoreInteractions(this.categoryRepository);
    }

    @Test
    public void testFind_shouldFindAllWithoutSearch() {
        final Pageable pageable = Pageable.unpaged();

        this.categoryService.find(null, pageable);

        verify(this.categoryRepository).findAll(pageable);
        verifyNoMoreInteractions(this.categoryRepository);
    }

    @Test
    public void testGet_ShouldThrowExceptionIfNameNotProvided() {
        assertThatExceptionOfType(CategoryNotFoundException.class)
                .isThrownBy(() -> this.categoryService.get(null))
                .withMessageContainingAll("empty", "name");
        verifyNoInteractions(this.categoryRepository);
    }

    @Test
    public void testGet_ShouldThrowExceptionIfCategoryNotFound() {
        final String name = "myCategory";

        doReturn(Optional.empty()).when(this.categoryRepository).findOneByName(name);

        assertThatExceptionOfType(CategoryNotFoundException.class)
                .isThrownBy(() -> this.categoryService.get(name))
                .withMessageContainingAll("no", "category", name);
        verify(this.categoryRepository).findOneByName(name);
        verifyNoMoreInteractions(this.categoryRepository);
    }

    @Test
    public void testGet_ShouldReturnCategoryIfFound() {
        final String name = "myCategory";
        final Category category = new Category(name);

        doReturn(Optional.of(category)).when(this.categoryRepository).findOneByName(name);

        Category categoryReturned = this.categoryService.get(name);
        assertThat(categoryReturned).isSameAs(category);

        verify(this.categoryRepository).findOneByName(name);
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
        final String name = "myCategory";
        final Category existingCategory = new Category(name);
        final Category newCategory = new Category(name);

        doReturn(Optional.of(existingCategory)).when(this.categoryRepository).findOneByName(name);

        assertThatExceptionOfType(CategoryAlreadyExistsException.class)
                .isThrownBy(() -> this.categoryService.create(newCategory))
                .withMessageContainingAll("category", "exists", name);
        verify(this.categoryRepository).findOneByName(name);
        verifyNoMoreInteractions(this.categoryRepository);
    }

    @Test
    public void testCreate_ShouldSaveNewCategoryIfNotAlreadyExists() {
        final ArgumentCaptor<Category> categoryCaptor = ArgumentCaptor.forClass(Category.class);

        final String name = "myCategory";
        final Category newCategory = new Category(name);

        doReturn(Optional.empty()).when(this.categoryRepository).findOneByName(name);

        this.categoryService.create(newCategory);

        verify(this.categoryRepository).findOneByName(name);
        verify(this.categoryRepository).save(categoryCaptor.capture());
        assertThat(categoryCaptor.getValue()).extracting("name").isEqualTo(name);
        verifyNoMoreInteractions(this.categoryRepository);
    }

    @Test
    public void testUpdate_ShouldThrowExceptionIfUpdateNotProvided() {
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> this.categoryService.update("category", null))
                .withMessageContainingAll("data", "null");
        verifyNoInteractions(this.categoryRepository);
    }

    @Test
    public void testUpdate_ShouldThrowExceptionIfNameNotProvided() {
        assertThatExceptionOfType(CategoryNotFoundException.class)
                .isThrownBy(() -> this.categoryService.update("", new Category(null)))
                .withMessageContainingAll("name", "empty");
        verifyNoInteractions(this.categoryRepository);
    }

    @Test
    public void testUpdate_ShouldThrowExceptionIfCategoryNotFound() {
        final String name = "myCategory";

        doReturn(Optional.empty()).when(this.categoryRepository).findOneByName(name);

        assertThatExceptionOfType(CategoryNotFoundException.class)
                .isThrownBy(() -> this.categoryService.update(name, new Category(null)))
                .withMessageContainingAll("no", "category", name);
        verify(this.categoryRepository).findOneByName(name);
        verifyNoMoreInteractions(this.categoryRepository);
    }

    @Test
    public void testUpdate_ShouldTriggerUpdateIfCategoryExists() {
        final ArgumentCaptor<Category> categoryCaptor = ArgumentCaptor.forClass(Category.class);

        final String name = "myCategory";
        final String newName = "newName";
        final Category existingCategory = new Category(name);
        final Category update = new Category(newName);

        doReturn(Optional.of(existingCategory)).when(this.categoryRepository).findOneByName(name);

        this.categoryService.update(name, update);

        verify(this.categoryRepository).findOneByName(name);
        verify(this.categoryRepository).save(categoryCaptor.capture());
        assertThat(categoryCaptor.getValue()).isSameAs(existingCategory);
        assertThat(categoryCaptor.getValue()).extracting("name").isEqualTo(newName);
        verifyNoMoreInteractions(this.categoryRepository);
    }

    @Test
    public void testDelete_ShouldDoNothingIfNameNotProvided() {
        this.categoryService.delete(null);
        verifyNoInteractions(this.categoryRepository);
    }

    @Test
    public void testDelete_ShouldDoNothingIfCategoryNotExists() {
        final String name = "myCategory";

        doReturn(Optional.empty()).when(this.categoryRepository).findOneByName(name);

        this.categoryService.delete(name);

        verify(this.categoryRepository).findOneByName(name);
        verifyNoMoreInteractions(this.categoryRepository);
    }

    @Test
    public void testDelete_ShouldDeleteCategoryIfExists() {
        final String name = "myCategory";
        final Category existingCategory = new Category(name);

        doReturn(Optional.of(existingCategory)).when(this.categoryRepository).findOneByName(name);

        this.categoryService.delete(name);

        verify(this.categoryRepository).findOneByName(name);
        verify(this.categoryRepository).delete(same(existingCategory));
        verifyNoMoreInteractions(this.categoryRepository);
    }
}
