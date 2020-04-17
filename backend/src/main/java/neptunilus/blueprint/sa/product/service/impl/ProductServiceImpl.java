package neptunilus.blueprint.sa.product.service.impl;

import neptunilus.blueprint.sa.product.exception.ProductAlreadyExistsException;
import neptunilus.blueprint.sa.product.exception.ProductNotFoundException;
import neptunilus.blueprint.sa.product.model.Category;
import neptunilus.blueprint.sa.product.model.Product;
import neptunilus.blueprint.sa.product.repository.ProductRepository;
import neptunilus.blueprint.sa.product.service.CategoryService;
import neptunilus.blueprint.sa.product.service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.Optional;

/**
 * Concrete implementation of {@link ProductService}.
 */
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryService categoryService;

    public ProductServiceImpl(final ProductRepository productRepository, final CategoryService categoryService) {
        this.productRepository = productRepository;
        this.categoryService = categoryService;
    }

    @Transactional(readOnly = true)
    @Override
    public Page<Product> find(final String search, final Category category, final Pageable pageable) {
        final boolean hasSearch = search != null && !search.isBlank();

        if (category != null) {
            final Category categoryFetched = this.categoryService.get(category.getName());
            return hasSearch ?
                    this.productRepository.findByNameContainingIgnoreCaseAndCategory(search, categoryFetched, pageable) :
                    this.productRepository.findByCategory(categoryFetched, pageable);
        }

        return hasSearch ?
                this.productRepository.findByNameContainingIgnoreCase(search, pageable) :
                this.productRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    @Override
    public Product get(final String name) throws ProductNotFoundException {
        if (name == null || name.isBlank()) {
            throw new ProductNotFoundException("no product with empty name possible");
        }

        final Optional<Product> product = this.productRepository.findOneByName(name);
        return product.orElseThrow(() -> new ProductNotFoundException(String.format("no product found with name '%s'", name)));
    }

    @Transactional
    @Override
    public void create(final Product product) throws ProductAlreadyExistsException {
        Assert.notNull(product, "product must not be null");

        final Optional<Product> existingProduct = this.productRepository.findOneByName(product.getName());
        if (existingProduct.isPresent()) {
            throw new ProductAlreadyExistsException(String.format("product with name '%s' already exists", product.getName()));
        }

        final Category categoryFetched = product.getCategory() != null ?
                this.categoryService.get(product.getCategory().getName()) : null;

        final Product newProduct = new Product(product.getName(), categoryFetched);
        this.productRepository.save(newProduct);
    }

    @Transactional
    @Override
    public void update(final String name, final Product update) throws ProductNotFoundException {
        Assert.notNull(update, "new data must not be null");

        final Category newCategory = update.getCategory() != null ?
                this.categoryService.get(update.getCategory().getName()) : null;

        final Product existingProduct = get(name);
        existingProduct.setName(update.getName());
        existingProduct.setCategory(newCategory);

        this.productRepository.save(existingProduct);
    }

    @Transactional
    @Override
    public void delete(final String name) {
        if (name == null || name.isBlank()) {
            return;
        }

        final Optional<Product> existingProduct = this.productRepository.findOneByName(name);
        existingProduct.ifPresent(this.productRepository::delete);
    }
}
