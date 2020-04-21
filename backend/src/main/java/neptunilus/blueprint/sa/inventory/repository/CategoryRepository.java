package neptunilus.blueprint.sa.inventory.repository;

import neptunilus.blueprint.sa.inventory.model.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * The repository for the {@link Category}s.
 */
@Repository
public interface CategoryRepository extends JpaRepository<Category, UUID> {

    /**
     * Returns the {@link Category} with the given name or {@link Optional#empty()} if none found.
     *
     * @param name The name
     * @return The category
     */
    Optional<Category> findOneByName(String name);

    /**
     * Returns all {@link Category}s containing the search in name.
     *
     * @param search   The search
     * @param pageable The pagination information
     * @return The categories
     */
    Page<Category> findByNameContainingIgnoreCase(String search, Pageable pageable);
}
