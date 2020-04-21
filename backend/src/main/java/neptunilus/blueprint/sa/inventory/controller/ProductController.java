package neptunilus.blueprint.sa.inventory.controller;

import neptunilus.blueprint.sa.inventory.controller.in.ProductCreateRequest;
import neptunilus.blueprint.sa.inventory.controller.in.ProductUpdateRequest;
import neptunilus.blueprint.sa.inventory.controller.out.ProductResponse;
import neptunilus.blueprint.sa.inventory.model.Product;
import neptunilus.blueprint.sa.inventory.service.ProductService;
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
 * Controller for handling {@link Product}s.
 */
@RestController
@RequestMapping("/product")
public class ProductController {

    private final ProductService productService;
    private final ModelMapper modelMapper;

    public ProductController(final ProductService productService, final ModelMapper modelMapper) {
        this.productService = productService;
        this.modelMapper = modelMapper;
    }

    @GetMapping
    public Page<ProductResponse> search(@RequestParam(required = false) final String search,
                                        @RequestParam(required = false) final UUID categoryId,
                                        final Pageable pageable) {
        final Page<Product> products = this.productService.find(search, false, categoryId, pageable);
        return products.map(product -> this.modelMapper.map(product, ProductResponse.class));
    }

    @GetMapping("/{id}")
    public ProductResponse get(@PathVariable final UUID id) {
        final Product product = this.productService.get(id);
        return this.modelMapper.map(product, ProductResponse.class);
    }

    @PostMapping
    public ResponseEntity<Void> create(@Valid @RequestBody final ProductCreateRequest productRequest) {
        final Product product = this.modelMapper.map(productRequest, Product.class);
        final UUID id = this.productService.create(product);

        final URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(id).toUri();

        return ResponseEntity.created(location).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@PathVariable final UUID id, @Valid @RequestBody final ProductUpdateRequest productRequest) {
        final Product update = this.modelMapper.map(productRequest, Product.class);
        this.productService.update(id, update);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus
    public ResponseEntity<Void> delete(@PathVariable final UUID id) {
        this.productService.delete(id);

        return ResponseEntity.noContent().build();
    }
}
