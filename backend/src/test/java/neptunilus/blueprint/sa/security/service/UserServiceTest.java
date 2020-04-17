package neptunilus.blueprint.sa.security.service;

import neptunilus.blueprint.sa.security.exception.UserAlreadyExistsException;
import neptunilus.blueprint.sa.security.exception.UserNotFoundException;
import neptunilus.blueprint.sa.security.model.User;
import neptunilus.blueprint.sa.security.model.UserRole;
import neptunilus.blueprint.sa.security.repository.UserRepository;
import neptunilus.blueprint.sa.security.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    private final UserRepository userRepository;
    private final UserRoleService userRoleService;
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;

    UserServiceTest(@Mock final UserRepository userRepository,
                    @Mock final UserRoleService userRoleService,
                    @Mock final PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userRoleService = userRoleService;
        this.passwordEncoder = passwordEncoder;
        this.userService = new UserServiceImpl(this.userRepository, this.userRoleService, this.passwordEncoder);
    }

    @Test
    public void testFind_shouldFindSomeWithSearch() {
        final String search = "search";
        final Pageable pageable = Pageable.unpaged();

        this.userService.find(search, pageable);

        verify(this.userRepository).findByEmailContainingIgnoreCase(search, pageable);
        verifyNoMoreInteractions(this.userRepository, this.userRoleService, this.passwordEncoder);
    }

    @Test
    public void testFind_shouldFindAllWithoutSearch() {
        final Pageable pageable = Pageable.unpaged();

        this.userService.find(null, pageable);

        verify(this.userRepository).findAll(same(pageable));
        verifyNoMoreInteractions(this.userRepository, this.userRoleService, this.passwordEncoder);
    }

    @Test
    public void testGet_ShouldThrowExceptionIfEmailNotProvided() {
        assertThatExceptionOfType(UserNotFoundException.class)
                .isThrownBy(() -> this.userService.get(null))
                .withMessageContainingAll("empty", "email");
        verifyNoInteractions(this.userRepository, this.userRoleService, this.passwordEncoder);
    }

    @Test
    public void testGet_ShouldThrowExceptionIfUserNotFound() {
        final String email = "my@mail.xy";

        doReturn(Optional.empty()).when(this.userRepository).findOneByEmail(email);

        assertThatExceptionOfType(UserNotFoundException.class)
                .isThrownBy(() -> this.userService.get(email))
                .withMessageContainingAll("no", "user", email);
        verify(this.userRepository).findOneByEmail(email);
        verifyNoMoreInteractions(this.userRepository, this.userRoleService, this.passwordEncoder);
    }

    @Test
    public void testGet_ShouldReturnUserIfFound() {
        final String email = "me@mail.xy";
        final User user = new User(email, null, null);

        doReturn(Optional.of(user)).when(this.userRepository).findOneByEmail(email);

        User userReturned = this.userService.get(email);
        assertThat(userReturned).isSameAs(user);

        verify(this.userRepository).findOneByEmail(email);
        verifyNoMoreInteractions(this.userRepository, this.userRoleService, this.passwordEncoder);
    }

    @Test
    public void testCreate_ShouldThrowExceptionIfUserNotProvided() {
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> this.userService.create(null))
                .withMessageContainingAll("user", "null");
        verifyNoInteractions(this.userRepository, this.userRoleService, this.passwordEncoder);
    }

    @Test
    public void testCreate_ShouldThrowExceptionIfUserAlreadyExists() {
        final String email = "me@mail.xy";
        final User existingUser = new User(email, null, null);
        final User newUser = new User(email, null, null);

        doReturn(Optional.of(existingUser)).when(this.userRepository).findOneByEmail(email);

        assertThatExceptionOfType(UserAlreadyExistsException.class)
                .isThrownBy(() -> this.userService.create(newUser))
                .withMessageContainingAll("user", "exists", email);
        verify(this.userRepository).findOneByEmail(email);
        verifyNoMoreInteractions(this.userRepository, this.userRoleService, this.passwordEncoder);
    }

    @Test
    public void testCreate_ShouldSaveNewUserIfNotAlreadyExists() {
        final ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

        final String email = "me@mail.xy";
        final String password = "password";
        final UserRole userRole = new UserRole("role", Collections.emptySet());
        final User newUser = new User(email, password, userRole);

        final UserRole existingUserRole = new UserRole("role", Collections.emptySet());
        doReturn(existingUserRole).when(this.userRoleService).get(userRole.getName());

        doReturn(Optional.empty()).when(this.userRepository).findOneByEmail(email);

        final String passwordEncoded = UUID.randomUUID().toString();
        doReturn(passwordEncoded).when(this.passwordEncoder).encode(password);

        this.userService.create(newUser);

        verify(this.userRepository).findOneByEmail(email);
        verify(this.userRoleService).get(userRole.getName());
        verify(this.passwordEncoder).encode(password);
        verify(this.userRepository).save(userCaptor.capture());
        assertThat(userCaptor.getValue()).extracting("email").isEqualTo(email);
        assertThat(userCaptor.getValue()).extracting("password").isEqualTo(passwordEncoded);
        assertThat(userCaptor.getValue()).extracting("role").isSameAs(existingUserRole);
        verifyNoMoreInteractions(this.userRepository, this.userRoleService, this.passwordEncoder);
    }

    @Test
    public void testUpdate_ShouldThrowExceptionIfUpdateNotProvided() {
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> this.userService.update("email", null))
                .withMessageContainingAll("data", "null");
        verifyNoInteractions(this.userRepository, this.userRoleService, this.passwordEncoder);
    }

    @Test
    public void testUpdate_ShouldThrowExceptionIfEmailNotProvided() {
        assertThatExceptionOfType(UserNotFoundException.class)
                .isThrownBy(() -> this.userService.update("", new User(null, null, null)))
                .withMessageContainingAll("email", "empty");
        verifyNoInteractions(this.userRepository, this.userRoleService, this.passwordEncoder);
    }

    @Test
    public void testUpdate_ShouldThrowExceptionIfUserNotFound() {
        final String email = "me@mail.xy";

        doReturn(Optional.empty()).when(this.userRepository).findOneByEmail(email);

        assertThatExceptionOfType(UserNotFoundException.class)
                .isThrownBy(() -> this.userService.update(email, new User(null, null, null)))
                .withMessageContainingAll("no", "user", email);
        verify(this.userRepository).findOneByEmail(email);
        verifyNoMoreInteractions(this.userRepository, this.userRoleService, this.passwordEncoder);
    }

    @Test
    public void testUpdate_ShouldTriggerUpdateIfProductExistsWithPasswordChange() {
        final ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

        final String email = "me@mail.xy";
        final String password = UUID.randomUUID().toString();
        final String newEmail = "new@abc.xy";
        final String newPassword = "password";
        final UserRole newUserRole = new UserRole("role", Collections.emptySet());
        final User existingUser = new User(email, password, new UserRole("roleOld", Collections.emptySet()));
        final User update = new User(newEmail, newPassword, newUserRole);

        final UserRole existingUserRole = new UserRole("role", Collections.emptySet());
        doReturn(existingUserRole).when(this.userRoleService).get(newUserRole.getName());

        final String newPasswordEncoded = UUID.randomUUID().toString();
        doReturn(newPasswordEncoded).when(this.passwordEncoder).encode(newPassword);

        doReturn(Optional.of(existingUser)).when(this.userRepository).findOneByEmail(email);

        this.userService.update(email, update);

        verify(this.userRepository).findOneByEmail(email);
        verify(this.userRoleService).get(newUserRole.getName());
        verify(this.passwordEncoder).encode(newPassword);
        verify(this.userRepository).save(userCaptor.capture());
        assertThat(userCaptor.getValue()).isSameAs(existingUser);
        assertThat(userCaptor.getValue()).extracting("email").isEqualTo(newEmail);
        assertThat(userCaptor.getValue()).extracting("password").isEqualTo(newPasswordEncoded);
        assertThat(userCaptor.getValue()).extracting("role").isSameAs(existingUserRole);
        verifyNoMoreInteractions(this.userRepository, this.userRoleService, this.passwordEncoder);
    }

    @Test
    public void testUpdate_ShouldTriggerUpdateIfProductExistsWithoutPasswordChange() {
        final ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

        final String email = "me@mail.xy";
        final String password = UUID.randomUUID().toString();
        final String newEmail = "new@abc.xy";
        final UserRole newUserRole = new UserRole("role", Collections.emptySet());
        final User existingUser = new User(email, password, new UserRole("roleOld", Collections.emptySet()));
        final User update = new User(newEmail, null, newUserRole);

        final UserRole existingUserRole = new UserRole("role", Collections.emptySet());
        doReturn(existingUserRole).when(this.userRoleService).get(newUserRole.getName());

        doReturn(Optional.of(existingUser)).when(this.userRepository).findOneByEmail(email);

        this.userService.update(email, update);

        verify(this.userRepository).findOneByEmail(email);
        verify(this.userRoleService).get(newUserRole.getName());
        verify(this.userRepository).save(userCaptor.capture());
        assertThat(userCaptor.getValue()).isSameAs(existingUser);
        assertThat(userCaptor.getValue()).extracting("email").isEqualTo(newEmail);
        assertThat(userCaptor.getValue()).extracting("password").isEqualTo(password);
        assertThat(userCaptor.getValue()).extracting("role").isSameAs(existingUserRole);
        verifyNoMoreInteractions(this.userRepository, this.userRoleService, this.passwordEncoder);
    }

    @Test
    public void testDelete_ShouldDoNothingIfEmailNotProvided() {
        this.userService.delete(null);
        verifyNoInteractions(this.userRepository, this.userRoleService, this.passwordEncoder);
    }

    @Test
    public void testDelete_ShouldDoNothingIfUserNotExists() {
        final String email = "me@mail.xy";

        doReturn(Optional.empty()).when(this.userRepository).findOneByEmail(email);

        this.userService.delete(email);

        verify(this.userRepository).findOneByEmail(email);
        verifyNoMoreInteractions(this.userRepository, this.userRoleService, this.passwordEncoder);
    }

    @Test
    public void testDelete_ShouldDeleteUserIfExists() {
        final String email = "me@mail.xy";
        final User existingUser = new User(email, null, null);

        doReturn(Optional.of(existingUser)).when(this.userRepository).findOneByEmail(email);

        this.userService.delete(email);

        verify(this.userRepository).findOneByEmail(email);
        verify(this.userRepository).delete(same(existingUser));
        verifyNoMoreInteractions(this.userRepository, this.userRoleService, this.passwordEncoder);
    }
}
