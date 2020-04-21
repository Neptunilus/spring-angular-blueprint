package neptunilus.blueprint.sa.inventory.controller;

import neptunilus.blueprint.sa.inventory.controller.in.CategoryCreateRequest;
import neptunilus.blueprint.sa.inventory.controller.in.CategoryUpdateRequest;
import neptunilus.blueprint.sa.inventory.controller.out.CategoryResponse;
import neptunilus.blueprint.sa.inventory.model.Category;
import neptunilus.blueprint.sa.inventory.service.CategoryService;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.UUID;

/**
 * Controller for handling {@link Category}s.
 */
@RestController
@RequestMapping("/category")
public class CategoryController {

    private final CategoryService categoryService;
    private final ModelMapper modelMapper;

    public CategoryController(final CategoryService categoryService, final ModelMapper modelMapper) {
        this.categoryService = categoryService;
        this.modelMapper = modelMapper;
    }

    @GetMapping
    public Page<CategoryResponse> search(@RequestParam(required = false) final String search, final Pageable pageable) {
        final Page<Category> categories = this.categoryService.find(search, false, pageable);
        return categories.map(category -> this.modelMapper.map(category, CategoryResponse.class));
    }

    @GetMapping("/{id}")
    public CategoryResponse get(@PathVariable final UUID id) {
        final Category category = this.categoryService.get(id);
        return this.modelMapper.map(category, CategoryResponse.class);
    }

    @PostMapping
    public ResponseEntity<Void> create(@Valid @RequestBody final CategoryCreateRequest categoryRequest) {
        final Category category = this.modelMapper.map(categoryRequest, Category.class);
        final UUID id = this.categoryService.create(category);

        final URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(id).toUri();

        return ResponseEntity.created(location).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@PathVariable final UUID id, @Valid @RequestBody final CategoryUpdateRequest categoryRequest) {
        final Category update = this.modelMapper.map(categoryRequest, Category.class);
        this.categoryService.update(id, update);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus
    public ResponseEntity<Void> delete(@PathVariable final UUID id) {
        this.categoryService.delete(id);

        return ResponseEntity.noContent().build();
    }
}
