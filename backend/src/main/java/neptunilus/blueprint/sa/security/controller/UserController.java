package neptunilus.blueprint.sa.security.controller;


import neptunilus.blueprint.sa.security.controller.in.UserRequest;
import neptunilus.blueprint.sa.security.controller.out.UserResponse;
import neptunilus.blueprint.sa.security.model.User;
import neptunilus.blueprint.sa.security.service.UserService;
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
 * Controller for handling {@link User}s.
 */
@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    private final ModelMapper modelMapper;

    public UserController(final UserService userService, final ModelMapper modelMapper) {
        this.userService = userService;
        this.modelMapper = modelMapper;
    }

    @GetMapping
    public Page<UserResponse> search(@RequestParam(required = false) final String search, final Pageable pageable) {
        final Page<User> users = this.userService.find(search, false, pageable);
        return users.map(user -> this.modelMapper.map(user, UserResponse.class));
    }

    @GetMapping("/{id}")
    public UserResponse get(@PathVariable final UUID id) {
        final User user = this.userService.get(id);
        return this.modelMapper.map(user, UserResponse.class);
    }

    @PostMapping
    public ResponseEntity<Void> create(@Valid @RequestBody final UserRequest userRequest) {
        final User user = this.modelMapper.map(userRequest, User.class);
        final UUID id = this.userService.create(user);

        final URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(id).toUri();

        return ResponseEntity.created(location).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@PathVariable final UUID id, @Valid @RequestBody final UserRequest userRequest) {
        final User update = this.modelMapper.map(userRequest, User.class);
        this.userService.update(id, update);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus
    public ResponseEntity<Void> delete(@PathVariable final UUID id) {
        this.userService.delete(id);

        return ResponseEntity.noContent().build();
    }
}
