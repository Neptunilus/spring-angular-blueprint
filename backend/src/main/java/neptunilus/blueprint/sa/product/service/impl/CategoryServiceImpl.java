package neptunilus.blueprint.sa.product.service.impl;

import neptunilus.blueprint.sa.product.exception.CategoryAlreadyExistsException;
import neptunilus.blueprint.sa.product.exception.CategoryNotFoundException;
import neptunilus.blueprint.sa.product.model.Category;
import neptunilus.blueprint.sa.product.repository.CategoryRepository;
import neptunilus.blueprint.sa.product.service.CategoryService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.Optional;

/**
 * Concrete implementation of {@link CategoryService}.
 */
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryServiceImpl(final CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Transactional(readOnly = true)
    @Override
    public Page<Category> find(final String search, final Pageable pageable) {
        if (search == null || search.isBlank()) {
            return this.categoryRepository.findAll(pageable);
        }
        return this.categoryRepository.findByNameContainingIgnoreCase(search, pageable);
    }

    @Transactional(readOnly = true)
    @Override
    public Category get(final String name) throws CategoryNotFoundException {
        if (name == null || name.isBlank()) {
            throw new CategoryNotFoundException("no category with empty name possible");
        }

        final Optional<Category> category = this.categoryRepository.findOneByName(name);
        return category.orElseThrow(() -> new CategoryNotFoundException(String.format("no category found with name '%s'", name)));
    }

    @Transactional
    @Override
    public void create(final Category category) throws CategoryAlreadyExistsException {
        Assert.notNull(category, "category must not be null");

        final Optional<Category> existingCategory = this.categoryRepository.findOneByName(category.getName());
        if (existingCategory.isPresent()) {
            throw new CategoryAlreadyExistsException(String.format("category with name '%s' already exists", category.getName()));
        }

        final Category newCategory = new Category(category.getName());
        this.categoryRepository.save(newCategory);
    }

    @Transactional
    @Override
    public void update(final String name, final Category update) throws CategoryNotFoundException {
        Assert.notNull(update, "new data must not be null");

        final Category existingCategory = get(name);
        existingCategory.setName(update.getName());

        this.categoryRepository.save(existingCategory);
    }

    @Transactional
    @Override
    public void delete(final String name) {
        if (name == null || name.isBlank()) {
            return;
        }

        final Optional<Category> existingCategory = this.categoryRepository.findOneByName(name);
        existingCategory.ifPresent(this.categoryRepository::delete);
    }
}
