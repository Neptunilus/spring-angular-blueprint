package neptunilus.blueprint.sa.security.controller;

import neptunilus.blueprint.sa.security.controller.out.UserRoleResponse;
import neptunilus.blueprint.sa.security.model.UserRole;
import neptunilus.blueprint.sa.security.service.UserRoleService;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * Controller for handling {@link UserRole}s.
 */
@RestController
@RequestMapping("/userrole")
public class UserRoleController {

    private final UserRoleService userRoleService;
    private final ModelMapper modelMapper;

    public UserRoleController(final UserRoleService userRoleService, final ModelMapper modelMapper) {
        this.userRoleService = userRoleService;
        this.modelMapper = modelMapper;
    }

    @GetMapping
    public Page<UserRoleResponse> search(final Pageable pageable) {
        final Page<UserRole> userRoles = this.userRoleService.find(pageable);
        return userRoles.map(userRole -> this.modelMapper.map(userRole, UserRoleResponse.class));
    }

    @GetMapping("/{id}")
    public UserRoleResponse get(@PathVariable final UUID id) {
        final UserRole userRole = this.userRoleService.get(id);
        return this.modelMapper.map(userRole, UserRoleResponse.class);
    }

}
