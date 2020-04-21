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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
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
    public void testFind_shouldFindSomeWithSearchAndWithStrict() {
        String search = "search";
        boolean strict = true;
        Pageable pageable = Pageable.unpaged();

        User existingUser = new User("me@mail.xy", "password", null);
        doReturn(Optional.of(existingUser)).when(this.userRepository).findOneByEmail(search);

        Page<User> page = this.userService.find(search, strict, pageable);

        assertThat(page).hasSize(1);
        assertThat(page).extracting("email").containsExactly("me@mail.xy");

        verify(this.userRepository).findOneByEmail(search);
        verifyNoMoreInteractions(this.userRepository, this.userRoleService, this.passwordEncoder);
    }

    @Test
    public void testFind_shouldFindSomeWithSearchAndWithoutStrict() {
        String search = "search";
        boolean strict = false;
        Pageable pageable = Pageable.unpaged();

        User existingUser = new User("me@mail.xy", "password", null);
        doReturn(new PageImpl<>(Collections.singletonList(existingUser))).when(this.userRepository).findByEmailContainingIgnoreCase(search, pageable);

        Page<User> page = this.userService.find(search, strict, pageable);

        assertThat(page).hasSize(1);
        assertThat(page).extracting("email").containsExactly("me@mail.xy");

        verify(this.userRepository).findByEmailContainingIgnoreCase(search, pageable);
        verifyNoMoreInteractions(this.userRepository, this.userRoleService, this.passwordEncoder);
    }

    @Test
    public void testFind_shouldFindAllWithoutSearchAndWithStrict() {
        String search = null;
        boolean strict = true;
        Pageable pageable = Pageable.unpaged();

        User existingUser = new User("me@mail.xy", "password", null);
        doReturn(new PageImpl<>(Collections.singletonList(existingUser))).when(this.userRepository).findAll(pageable);

        Page<User> page = this.userService.find(search, strict, pageable);

        assertThat(page).hasSize(1);
        assertThat(page).extracting("email").containsExactly("me@mail.xy");

        verify(this.userRepository).findAll(pageable);
        verifyNoMoreInteractions(this.userRepository, this.userRoleService, this.passwordEncoder);
    }

    @Test
    public void testFind_shouldFindAllWithoutSearchAndWithoutStrict() {
        String search = null;
        boolean strict = false;
        Pageable pageable = Pageable.unpaged();

        User existingUser = new User("me@mail.xy", "password", null);
        doReturn(new PageImpl<>(Collections.singletonList(existingUser))).when(this.userRepository).findAll(pageable);

        Page<User> page = this.userService.find(search, strict, pageable);

        assertThat(page).hasSize(1);
        assertThat(page).extracting("email").containsExactly("me@mail.xy");

        verify(this.userRepository).findAll(pageable);
        verifyNoMoreInteractions(this.userRepository, this.userRoleService, this.passwordEncoder);
    }

    @Test
    public void testGet_ShouldThrowExceptionIfIdNotProvided() {
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> this.userService.get(null))
                .withMessageContainingAll("id", "null");
        verifyNoInteractions(this.userRepository, this.userRoleService, this.passwordEncoder);
    }

    @Test
    public void testGet_ShouldThrowExceptionIfUserNotFound() {
        UUID id = UUID.randomUUID();

        doReturn(Optional.empty()).when(this.userRepository).findById(id);

        assertThatExceptionOfType(UserNotFoundException.class)
                .isThrownBy(() -> this.userService.get(id))
                .withMessageContainingAll("no", "user", id.toString());
        verify(this.userRepository).findById(id);
        verifyNoMoreInteractions(this.userRepository, this.userRoleService, this.passwordEncoder);
    }

    @Test
    public void testGet_ShouldReturnUserIfFound() {
        UUID id = UUID.randomUUID();
        User user = new User("my@mail.xy", null, null);

        doReturn(Optional.of(user)).when(this.userRepository).findById(id);

        User userReturned = this.userService.get(id);
        assertThat(userReturned).isSameAs(user);

        verify(this.userRepository).findById(id);
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
        String email = "me@mail.xy";
        User existingUser = new User(email, null, null);
        User newUser = new User(email, null, null);

        doReturn(Optional.of(existingUser)).when(this.userRepository).findOneByEmail(email);

        assertThatExceptionOfType(UserAlreadyExistsException.class)
                .isThrownBy(() -> this.userService.create(newUser))
                .withMessageContainingAll("user", "exists", email);
        verify(this.userRepository).findOneByEmail(email);
        verifyNoMoreInteractions(this.userRepository, this.userRoleService, this.passwordEncoder);
    }

    @Test
    public void testCreate_ShouldSaveNewUserIfNotAlreadyExists() {
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

        String email = "me@mail.xy";
        String password = "password";

        UUID userRoleId = UUID.randomUUID();
        UserRole userRole = new UserRole("role", Collections.emptySet());
        userRole.setId(userRoleId);

        User newUser = new User(email, password, userRole);

        UserRole existingUserRole = new UserRole("role", Collections.emptySet());
        doReturn(existingUserRole).when(this.userRoleService).get(userRoleId);

        doReturn(Optional.empty()).when(this.userRepository).findOneByEmail(email);

        String passwordEncoded = UUID.randomUUID().toString();
        doReturn(passwordEncoded).when(this.passwordEncoder).encode(password);

        User persistedUser = new User("my@mail.xy", null, null);
        persistedUser.setId(UUID.randomUUID());

        doReturn(persistedUser).when(this.userRepository).save(any(User.class));

        UUID newId = this.userService.create(newUser);

        verify(this.userRepository).findOneByEmail(email);
        verify(this.userRoleService).get(userRoleId);
        verify(this.passwordEncoder).encode(password);
        verify(this.userRepository).save(userCaptor.capture());
        assertThat(newId).isEqualTo(persistedUser.getId());
        assertThat(userCaptor.getValue()).extracting("email").isEqualTo(email);
        assertThat(userCaptor.getValue()).extracting("password").isEqualTo(passwordEncoded);
        assertThat(userCaptor.getValue()).extracting("role").isSameAs(existingUserRole);
        verifyNoMoreInteractions(this.userRepository, this.userRoleService, this.passwordEncoder);
    }

    @Test
    public void testUpdate_ShouldThrowExceptionIfIdNotProvided() {
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> this.userService.update(null, new User(null, null, null)))
                .withMessageContainingAll("id", "null");
        verifyNoInteractions(this.userRepository, this.userRoleService, this.passwordEncoder);
    }

    @Test
    public void testUpdate_ShouldThrowExceptionIfUpdateNotProvided() {
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> this.userService.update(UUID.randomUUID(), null))
                .withMessageContainingAll("data", "null");
        verifyNoInteractions(this.userRepository, this.userRoleService, this.passwordEncoder);
    }

    @Test
    public void testUpdate_ShouldThrowExceptionIfUserNotFound() {
        UUID id = UUID.randomUUID();

        doReturn(Optional.empty()).when(this.userRepository).findById(id);

        assertThatExceptionOfType(UserNotFoundException.class)
                .isThrownBy(() -> this.userService.update(id, new User(null, null, null)))
                .withMessageContainingAll("no", "user", id.toString());
        verify(this.userRepository).findById(id);
        verifyNoMoreInteractions(this.userRepository, this.userRoleService, this.passwordEncoder);
    }

    @Test
    public void testUpdate_ShouldThrowExceptionIfNewUserEmailAlreadyExists() {
        UUID id = UUID.randomUUID();
        String email = "me@mail.xy";
        String newEmail = "new@mail.xy";

        User update = new User(newEmail, null, null);

        User existingUser = new User(email, null, null);
        doReturn(Optional.of(existingUser)).when(this.userRepository).findById(id);

        User conflictingUser = new User(newEmail, null, null);
        doReturn(Optional.of(conflictingUser)).when(this.userRepository).findOneByEmail(newEmail);

        assertThatExceptionOfType(UserAlreadyExistsException.class)
                .isThrownBy(() -> this.userService.update(id, update))
                .withMessageContainingAll("user", "exists", newEmail);
        verify(this.userRepository).findById(id);
        verify(this.userRepository).findOneByEmail(newEmail);
        verifyNoMoreInteractions(this.userRepository, this.userRoleService, this.passwordEncoder);
    }

    @Test
    public void testUpdate_ShouldTriggerUpdateIfUserExistsWithPasswordChangeAndWithoutNewRole() {
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

        UUID id = UUID.randomUUID();
        String email = "me@mail.xy";
        String password = UUID.randomUUID().toString();
        String newEmail = "new@abc.xy";
        String newPassword = "password";

        UserRole existingUserRole = new UserRole("role", Collections.emptySet());
        User existingUser = new User(email, password, existingUserRole);

        User update = new User(newEmail, newPassword, null);

        String newPasswordEncoded = UUID.randomUUID().toString();
        doReturn(newPasswordEncoded).when(this.passwordEncoder).encode(newPassword);

        doReturn(Optional.of(existingUser)).when(this.userRepository).findById(id);
        doReturn(Optional.empty()).when(this.userRepository).findOneByEmail(newEmail);

        this.userService.update(id, update);

        verify(this.userRepository).findById(id);
        verify(this.userRepository).findOneByEmail(newEmail);
        verify(this.passwordEncoder).encode(newPassword);
        verify(this.userRepository).save(userCaptor.capture());
        assertThat(userCaptor.getValue()).isSameAs(existingUser);
        assertThat(userCaptor.getValue()).extracting("email").isEqualTo(newEmail);
        assertThat(userCaptor.getValue()).extracting("password").isEqualTo(newPasswordEncoded);
        assertThat(userCaptor.getValue()).extracting("role").isSameAs(existingUserRole);
        verifyNoMoreInteractions(this.userRepository, this.userRoleService, this.passwordEncoder);
    }

    @Test
    public void testUpdate_ShouldTriggerUpdateIfUserRoleExistsWithoutPasswordChange() {
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

        UUID id = UUID.randomUUID();
        String email = "me@mail.xy";
        String password = "password";
        String newEmail = "new@abc.xy";
        String newPassword = null;

        UUID newUserRoleId = UUID.randomUUID();
        UserRole newUserRole = new UserRole("role", Collections.emptySet());
        newUserRole.setId(newUserRoleId);

        User existingUser = new User(email, password, new UserRole("roleOld", Collections.emptySet()));
        User update = new User(newEmail, newPassword, newUserRole);

        UserRole existingUserRole = new UserRole("role", Collections.emptySet());
        doReturn(existingUserRole).when(this.userRoleService).get(newUserRoleId);

        doReturn(Optional.of(existingUser)).when(this.userRepository).findById(id);
        doReturn(Optional.empty()).when(this.userRepository).findOneByEmail(newEmail);

        this.userService.update(id, update);

        verify(this.userRepository).findById(id);
        verify(this.userRepository).findOneByEmail(newEmail);
        verify(this.userRoleService).get(newUserRole.getId());
        verify(this.userRepository).save(userCaptor.capture());
        assertThat(userCaptor.getValue()).isSameAs(existingUser);
        assertThat(userCaptor.getValue()).extracting("email").isEqualTo(newEmail);
        assertThat(userCaptor.getValue()).extracting("password").isEqualTo(password);
        assertThat(userCaptor.getValue()).extracting("role").isSameAs(existingUserRole);
        verifyNoMoreInteractions(this.userRepository, this.userRoleService, this.passwordEncoder);
    }

    @Test
    public void testDelete_ShouldDoNothingIfIdNotProvided() {
        this.userService.delete(null);
        verifyNoInteractions(this.userRepository, this.userRoleService, this.passwordEncoder);
    }

    @Test
    public void testDelete_ShouldDoNothingIfUserNotExists() {
        UUID id = UUID.randomUUID();

        doReturn(Optional.empty()).when(this.userRepository).findById(id);

        this.userService.delete(id);

        verify(this.userRepository).findById(id);
        verifyNoMoreInteractions(this.userRepository, this.userRoleService, this.passwordEncoder);
    }

    @Test
    public void testDelete_ShouldDeleteUserIfExists() {
        UUID id = UUID.randomUUID();
        User existingUser = new User("my@mail.xy", null, null);

        doReturn(Optional.of(existingUser)).when(this.userRepository).findById(id);

        this.userService.delete(id);

        verify(this.userRepository).findById(id);
        verify(this.userRepository).delete(same(existingUser));
        verifyNoMoreInteractions(this.userRepository, this.userRoleService, this.passwordEncoder);
    }
}
