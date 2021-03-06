package neptunilus.blueprint.sa.inventory.service.impl;

import neptunilus.blueprint.sa.inventory.exception.ProductAlreadyExistsException;
import neptunilus.blueprint.sa.inventory.exception.ProductNotFoundException;
import neptunilus.blueprint.sa.inventory.model.Category;
import neptunilus.blueprint.sa.inventory.model.Product;
import neptunilus.blueprint.sa.inventory.repository.ProductRepository;
import neptunilus.blueprint.sa.inventory.service.CategoryService;
import neptunilus.blueprint.sa.inventory.service.ProductService;
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
 * Concrete implementation of {@link ProductService}.
 */
@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryService categoryService;

    public ProductServiceImpl(final ProductRepository productRepository, final CategoryService categoryService) {
        this.productRepository = productRepository;
        this.categoryService = categoryService;
    }

    @Transactional(readOnly = true)
    @Override
    public Page<Product> find(final String search, final boolean strict, final UUID categoryId, final Pageable pageable) {
        final boolean hasSearch = StringUtils.isNotBlank(search);
        final boolean hasCategory = categoryId != null;

        final Category categoryFetched = hasCategory ? this.categoryService.get(categoryId) : null;

        if (!hasSearch) {
            return hasCategory ?
                    this.productRepository.findByCategory(categoryFetched, pageable) :
                    this.productRepository.findAll(pageable);
        }

        if (strict) {
            final Optional<Product> product = hasCategory ?
                    this.productRepository.findOneByNameAndCategory(search, categoryFetched) :
                    this.productRepository.findOneByName(search);
            return product.isPresent() ? new PageImpl<>(Collections.singletonList(product.get())) : Page.empty();
        }

        return hasCategory ?
                this.productRepository.findByNameContainingIgnoreCaseAndCategory(search, categoryFetched, pageable) :
                this.productRepository.findByNameContainingIgnoreCase(search, pageable);
    }

    @Transactional(readOnly = true)
    @Override
    public Product get(final UUID id) throws ProductNotFoundException {
        Assert.notNull(id, "id must not be null");

        final Optional<Product> product = this.productRepository.findById(id);
        return product.orElseThrow(() -> new ProductNotFoundException(String.format("no product found with id '%s'", id)));
    }

    @Transactional
    @Override
    public UUID create(final Product product) throws ProductAlreadyExistsException {
        Assert.notNull(product, "product must not be null");
        assertProductWithNameNotPresent(product.getName());

        final Category categoryFetched = product.getCategory() != null ?
                this.categoryService.get(product.getCategory().getId()) : null;

        Product newProduct = new Product(product.getName(), categoryFetched);
        newProduct = this.productRepository.save(newProduct);

        return newProduct.getId();
    }

    @Transactional
    @Override
    public void update(final UUID id, final Product update) throws ProductNotFoundException, ProductAlreadyExistsException {
        Assert.notNull(id, "id must not be null");
        Assert.notNull(update, "new data must not be null");

        final Product existingProduct = get(id);
        final Category newCategory = update.getCategory() != null ?
                this.categoryService.get(update.getCategory().getId()) : null;

        if (!Objects.equals(existingProduct.getName(), update.getName())) {
            assertProductWithNameNotPresent(update.getName());
        }

        existingProduct.setName(update.getName());
        existingProduct.setCategory(newCategory);

        this.productRepository.save(existingProduct);
    }

    @Transactional
    @Override
    public void delete(final UUID id) {
        if (id == null) {
            return;
        }

        final Optional<Product> existingProduct = this.productRepository.findById(id);
        existingProduct.ifPresent(this.productRepository::delete);
    }

    private void assertProductWithNameNotPresent(final String name) {
        final Optional<Product> existingProduct = this.productRepository.findOneByName(name);
        if (existingProduct.isPresent()) {
            throw new ProductAlreadyExistsException(String.format("product with name '%s' already exists", name));
        }
    }

}
