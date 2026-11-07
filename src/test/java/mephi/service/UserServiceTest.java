package mephi.service;

import mephi.dto.request.LoginRequest;
import mephi.dto.request.RegistrationRequest;
import mephi.exception.AuthenticationException;
import mephi.exception.PersonalFinanceException;
import mephi.exception.UserAlreadyExistsException;
import mephi.model.User;
import mephi.model.Wallet;
import mephi.repository.UserRepository;
import mephi.repository.WalletRepository;
import mephi.util.SessionManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private WalletRepository walletRepository;

    @Mock
    private SessionManager sessionManager;

    @InjectMocks
    private UserService userService;

    @Test
    void register_Success() {
        RegistrationRequest request = new RegistrationRequest("testUser", "password123");
        when(userRepository.findByLogin("testUser")).thenReturn(Optional.empty());

        User result = userService.register(request);

        assertNotNull(result);
        assertEquals("testUser", result.getLogin());

        verify(userRepository, times(1)).create(any(User.class));
        verify(walletRepository, times(1)).save(any(Wallet.class));
    }

    @Test
    void register_ThrowsUserAlreadyExistsException() {
        RegistrationRequest request = new RegistrationRequest("existingUser", "password123");
        User existingUser = new User("existingUser", "hashed_pwd");
        when(userRepository.findByLogin("existingUser")).thenReturn(Optional.of(existingUser));

        assertThrows(UserAlreadyExistsException.class, () -> userService.register(request));

        verify(userRepository, never()).create(any());
        verify(walletRepository, never()).save(any());
    }

    @Test
    void login_Success() {
        LoginRequest request = new LoginRequest("testUser", "password123");

        String fakeHash = mephi.util.PasswordEncoder.encode("password123");

        User user = new User("testUser", fakeHash);
        UUID expectedSessionId = UUID.randomUUID();

        when(userRepository.findByLogin("testUser")).thenReturn(Optional.of(user));
        when(sessionManager.createSession(user.getId())).thenReturn(expectedSessionId);

        UUID actualSessionId = userService.login(request);

        assertEquals(expectedSessionId, actualSessionId);
    }

    @Test
    void login_ThrowsAuthenticationException_WhenUserNotFound() {
        LoginRequest request = new LoginRequest("unknownUser", "pwd");
        when(userRepository.findByLogin("unknownUser")).thenReturn(Optional.empty());

        assertThrows(AuthenticationException.class, () -> userService.login(request));
    }

    @Test
    void logout_Success() {
        userService.logout();

        verify(sessionManager, times(1)).logout();
    }

    @Test
    void isLogged_ReturnsTrue() {
        when(sessionManager.isLogged()).thenReturn(true);

        assertTrue(userService.isLogged());
    }

    @Test
    void geCurrenttUser_Success() {
        UUID userId = UUID.randomUUID();
        User user = new User("login", "hash");
        when(sessionManager.getUserId()).thenReturn(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        User result = userService.geCurrenttUser();

        assertEquals(user, result);
    }

    @Test
    void geCurrenttUser_ThrowsPersonalFinanceException() {
        UUID userId = UUID.randomUUID();
        when(sessionManager.getUserId()).thenReturn(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(PersonalFinanceException.class, () -> userService.geCurrenttUser());
    }

    @Test
    void getWallet_Success() {
        UUID userId = UUID.randomUUID();
        Wallet wallet = new Wallet(userId);
        when(sessionManager.getUserId()).thenReturn(userId);
        when(walletRepository.findByUserId(userId)).thenReturn(Optional.of(wallet));

        Wallet result = userService.getWallet();

        assertEquals(wallet, result);
    }
}
