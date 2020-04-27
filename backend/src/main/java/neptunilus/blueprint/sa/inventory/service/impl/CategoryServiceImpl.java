package neptunilus.blueprint.sa.inventory.service.impl;

import neptunilus.blueprint.sa.inventory.exception.CategoryAlreadyExistsException;
import neptunilus.blueprint.sa.inventory.exception.CategoryNotFoundException;
import neptunilus.blueprint.sa.inventory.model.Category;
import neptunilus.blueprint.sa.inventory.repository.CategoryRepository;
import neptunilus.blueprint.sa.inventory.service.CategoryService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.Collections;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * Concrete implementation of {@link CategoryService}.
 */
@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryServiceImpl(final CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Transactional(readOnly = true)
    @Override
    public Page<Category> find(final String search, final boolean strict, final Pageable pageable) {
        if (StringUtils.isBlank(search)) {
            return this.categoryRepository.findAll(pageable);
        }
        if (strict) {
            final Optional<Category> category = this.categoryRepository.findOneByName(search);
            return category.isPresent() ? new PageImpl<>(Collections.singletonList(category.get())) : Page.empty();
        }
        return this.categoryRepository.findByNameContainingIgnoreCase(search, pageable);
    }

    @Transactional(readOnly = true)
    @Override
    public Category get(final UUID id) throws CategoryNotFoundException {
        Assert.notNull(id, "id must not be null");

        final Optional<Category> category = this.categoryRepository.findById(id);
        return category.orElseThrow(() -> new CategoryNotFoundException(String.format("no category found with id '%s'", id)));
    }

    @Transactional
    @Override
    public UUID create(final Category category) throws CategoryAlreadyExistsException {
        Assert.notNull(category, "category must not be null");
        assertCategoryWithNameNotPresent(category.getName());

        Category newCategory = new Category(category.getName());
        newCategory = this.categoryRepository.save(newCategory);

        return newCategory.getId();
    }

    @Transactional
    @Override
    public void update(final UUID id, final Category update) throws CategoryNotFoundException, CategoryAlreadyExistsException {
        Assert.notNull(id, "id must not be null");
        Assert.notNull(update, "new data must not be null");

        final Category existingCategory = get(id);

        if (!Objects.equals(existingCategory.getName(), update.getName())) {
            assertCategoryWithNameNotPresent(update.getName());
        }

        existingCategory.setName(update.getName());
        this.categoryRepository.save(existingCategory);
    }

    @Transactional
    @Override
    public void delete(final UUID id) {
        if (id == null) {
            return;
        }

        final Optional<Category> existingCategory = this.categoryRepository.findById(id);
        existingCategory.ifPresent(this.categoryRepository::delete);
    }

    private void assertCategoryWithNameNotPresent(final String name) {
        final Optional<Category> existingCategory = this.categoryRepository.findOneByName(name);
        if (existingCategory.isPresent()) {
            throw new CategoryAlreadyExistsException(String.format("category with name '%s' already exists", name));
        }
    }
}
