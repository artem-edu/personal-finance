package mephi.service;

import mephi.core.SessionManager;
import mephi.dto.request.LoginRequest;
import mephi.dto.request.RegistrationRequest;
import mephi.exception.AuthenticationException;
import mephi.exception.PersonalFinanceException;
import mephi.exception.UserAlreadyExistsException;
import mephi.model.User;
import mephi.model.Wallet;
import mephi.repository.UserRepository;
import mephi.repository.WalletRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private WalletRepository walletRepository;
    @Mock
    private SessionManager sessionManager;

    @InjectMocks
    private UserService userService;

    @Test
    public void registerSuccess() {
        RegistrationRequest request = new RegistrationRequest("newUser", "secretPassword");
        when(userRepository.findByLogin("newUser")).thenReturn(Optional.empty());

        User result = userService.register(request);

        assertNotNull(result);
        assertEquals("newUser", result.getLogin());
        assertNotEquals("secretPassword", result.getHasPassword());

        verify(userRepository, times(1)).create(any(User.class));
        verify(walletRepository, times(1)).save(any(Wallet.class));
    }

    @Test
    public void registerException() {
        RegistrationRequest request = new RegistrationRequest("existingUser", "secretPassword");
        User existingUser = new User("existingUser", "hashedPassword");
        when(userRepository.findByLogin("existingUser")).thenReturn(Optional.of(existingUser));

        UserAlreadyExistsException exception = assertThrows(UserAlreadyExistsException.class, () -> {
            userService.register(request);
        });

        assertEquals("existingUser", exception.getMessage());
        verify(userRepository, never()).create(any());
        verify(walletRepository, never()).save(any());
    }

    @Test
    public void loginSuccess() {
        LoginRequest request = new LoginRequest("testUser", "password123");
        String encodedPassword = mephi.util.PasswordEncoder.encode("password123");
        User user = new User("testUser", encodedPassword);
        UUID expectedSessionId = UUID.randomUUID();

        when(userRepository.findByLogin("testUser")).thenReturn(Optional.of(user));
        when(sessionManager.createSession(user.getId())).thenReturn(expectedSessionId);

        UUID actualSessionId = userService.login(request);

        assertEquals(expectedSessionId, actualSessionId);
        verify(sessionManager, times(1)).createSession(user.getId());
    }

    @Test
    public void loginExceptionUserNotFound() {
        LoginRequest request = new LoginRequest("unknownUser", "password123");
        when(userRepository.findByLogin("unknownUser")).thenReturn(Optional.empty());

        AuthenticationException exception = assertThrows(AuthenticationException.class, () -> {
            userService.login(request);
        });

        assertEquals("Неверный логин или пароль", exception.getMessage());
        verifyNoInteractions(sessionManager);
    }

    @Test
    public void loginExceptionPasswordIsWrong() {
        LoginRequest request = new LoginRequest("testUser", "wrongPassword");
        String encodedPassword = mephi.util.PasswordEncoder.encode("correctPassword");
        User user = new User("testUser", encodedPassword);

        when(userRepository.findByLogin("testUser")).thenReturn(Optional.of(user));

        AuthenticationException exception = assertThrows(AuthenticationException.class, () -> {
            userService.login(request);
        });

        assertEquals("Неверный логин или пароль", exception.getMessage());
        verifyNoInteractions(sessionManager);
    }

    @Test
    public void logout() {
        userService.logout();

        verify(sessionManager, times(1)).logout();
        verifyNoMoreInteractions(sessionManager);
    }

    @Test
    public void isLogged() {
        when(sessionManager.isLogged()).thenReturn(true);

        boolean result = userService.isLogged();

        assertTrue(result);
        verify(sessionManager, times(1)).isLogged();
    }

    @Test
    public void getCurrentUserSuccess() {
        UUID userId = UUID.randomUUID();
        User expectedUser = new User("testUser", "hashedPassword");
        when(sessionManager.getUserId()).thenReturn(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(expectedUser));

        User actualUser = userService.getCurrentUser();

        assertEquals(expectedUser, actualUser);
        verify(sessionManager, times(1)).getUserId();
        verify(userRepository, times(1)).findById(userId);
        verify(sessionManager, never()).logout();
    }

    @Test
    public void getCurrentUserException() {
        UUID userId = UUID.randomUUID();
        when(sessionManager.getUserId()).thenReturn(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        PersonalFinanceException exception = assertThrows(PersonalFinanceException.class, () -> {
            userService.getCurrentUser();
        });

        assertEquals("Требуется авторизация", exception.getMessage());
        verify(sessionManager, times(1)).logout();
    }

    @Test
    public void getWalletSuccess() {
        UUID userId = UUID.randomUUID();
        Wallet expectedWallet = new Wallet(userId);
        when(sessionManager.getUserId()).thenReturn(userId);
        when(walletRepository.findByUserId(userId)).thenReturn(Optional.of(expectedWallet));

        Wallet actualWallet = userService.getWallet();

        assertEquals(expectedWallet, actualWallet);
        verify(sessionManager, times(1)).getUserId();
        verify(walletRepository, times(1)).findByUserId(userId);
    }

    @Test
    public void getWalletException() {
        UUID userId = UUID.randomUUID();
        when(sessionManager.getUserId()).thenReturn(userId);
        when(walletRepository.findByUserId(userId)).thenReturn(Optional.empty());

        PersonalFinanceException exception = assertThrows(PersonalFinanceException.class, () -> {
            userService.getWallet();
        });

        assertEquals("Кошелек не найден", exception.getMessage());
        verify(sessionManager, times(1)).getUserId();
        verify(walletRepository, times(1)).findByUserId(userId);
    }
}
