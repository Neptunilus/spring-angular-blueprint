package neptunilus.blueprint.sa.security.service;

import neptunilus.blueprint.sa.security.exception.UserRoleNotFoundException;
import neptunilus.blueprint.sa.security.model.UserRole;
import neptunilus.blueprint.sa.security.repository.UserRoleRepository;
import neptunilus.blueprint.sa.security.service.impl.UserRoleServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserRoleServiceTest {

    private final UserRoleRepository userRoleRepository;
    private final UserRoleService userRoleService;

    UserRoleServiceTest(@Mock final UserRoleRepository userRoleRepository) {
        this.userRoleRepository = userRoleRepository;
        this.userRoleService = new UserRoleServiceImpl(userRoleRepository);
    }

    @Test
    public void testFind_shouldFindAll() {
        final Pageable pageable = Pageable.unpaged();

        this.userRoleService.find(pageable);

        verify(this.userRoleRepository).findAll(pageable);
        verifyNoMoreInteractions(this.userRoleRepository);
    }

    @Test
    public void testGet_ShouldThrowExceptionIfIdNotProvided() {
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> this.userRoleService.get(null))
                .withMessageContainingAll("id", "null");
        verifyNoInteractions(this.userRoleRepository);
    }

    @Test
    public void testGet_ShouldThrowExceptionIfUserRoleNotFound() {
        final UUID id = UUID.randomUUID();

        doReturn(Optional.empty()).when(this.userRoleRepository).findById(id);

        assertThatExceptionOfType(UserRoleNotFoundException.class)
                .isThrownBy(() -> this.userRoleService.get(id))
                .withMessageContainingAll("no", "user role", id.toString());
        verify(this.userRoleRepository).findById(id);
        verifyNoMoreInteractions(this.userRoleRepository);
    }

    @Test
    public void testGet_ShouldReturnUserRoleIfFound() {
        final UUID id = UUID.randomUUID();
        final UserRole userRole = new UserRole("user role", Collections.emptySet());

        doReturn(Optional.of(userRole)).when(this.userRoleRepository).findById(id);

        UserRole userRoleReturned = this.userRoleService.get(id);
        assertThat(userRoleReturned).isSameAs(userRole);

        verify(this.userRoleRepository).findById(id);
        verifyNoMoreInteractions(this.userRoleRepository);
    }
}
