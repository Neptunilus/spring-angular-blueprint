package neptunilus.blueprint.sa.product.repository;

import neptunilus.blueprint.sa.product.model.Category;
import neptunilus.blueprint.sa.product.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * The repository for the {@link Product}s.
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    /**
     * Returns the {@link Product} with the given name or {@link Optional#empty()} if none found.
     *
     * @param name The name
     * @return The category
     */
    Optional<Product> findOneByName(String name);

    /**
     * Returns all {@link Product}s containing the search in name.
     *
     * @param search   The search
     * @param pageable The pagination information
     * @return The products
     */
    Page<Product> findByNameContainingIgnoreCase(String search, Pageable pageable);

    /**
     * Returns all {@link Product}s belonging to a specific {@link Category}.
     *
     * @param category The category
     * @param pageable The pagination information
     * @return The products
     */
    Page<Product> findByCategory(Category category, Pageable pageable);
}
