package mephi.controller;

import mephi.dto.request.LoginRequest;
import mephi.dto.request.RegistrationRequest;
import mephi.exception.PersonalFinanceException;
import mephi.service.UserService;
import mephi.util.ConsoleReader;
import mephi.view.AuthView;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthControllerTest {
    @Mock
    private UserService userService;
    @Mock
    private AuthView view;

    @InjectMocks
    private AuthController authController;

    @Test
    public void helpEmptyArgs() {
        authController.handleCommand(List.of());

        verify(view, times(1)).printHelp();
    }

    @Test
    public void helpCommand() {
        authController.handleCommand(List.of("help"));

        verify(view, times(1)).printHelp();
    }

    @Test
    public void unknownAction() {
        authController.handleCommand(List.of("unknown"));

        verify(view, times(1)).printUnknownAction();
    }

    @Test
    public void loginSuccess() {
        when(userService.isLogged()).thenReturn(false);
        try (MockedStatic<ConsoleReader> mockedReader = mockStatic(ConsoleReader.class)) {
            mockedReader.when(() -> ConsoleReader.readString("Логин")).thenReturn("testUser");
            mockedReader.when(() -> ConsoleReader.readString("Пароль")).thenReturn("password123");

            authController.handleCommand(List.of("login"));

            ArgumentCaptor<LoginRequest> requestCaptor = ArgumentCaptor.forClass(LoginRequest.class);
            verify(userService, times(1)).login(requestCaptor.capture());

            LoginRequest request = requestCaptor.getValue();
            assertEquals("testUser", request.login());
            assertEquals("password123", request.password());

            verify(view, times(1)).printLoginSuccess("testUser");
        }
    }

    @Test
    public void loginWhenAlreadyLogged() {
        when(userService.isLogged()).thenReturn(true);
        try (MockedStatic<ConsoleReader> mockedReader = mockStatic(ConsoleReader.class)) {
            authController.handleCommand(List.of("login"));

            verify(view, times(1)).printAlreadyLogged();
            verify(userService, never()).login(any(LoginRequest.class));
            mockedReader.verifyNoInteractions();
        }
    }

    @Test
    public void loginException() {
        when(userService.isLogged()).thenReturn(false);
        PersonalFinanceException exception = new PersonalFinanceException("Неверный логин или пароль");
        doThrow(exception).when(userService).login(any(LoginRequest.class));
        try (MockedStatic<ConsoleReader> mockedReader = mockStatic(ConsoleReader.class)) {
            mockedReader.when(() -> ConsoleReader.readString("Логин")).thenReturn("testUser");
            mockedReader.when(() -> ConsoleReader.readString("Пароль")).thenReturn("password123");

            authController.handleCommand(List.of("login"));

            verify(view, times(1)).printError(exception);
            verify(view, never()).printLoginSuccess(anyString());
        }
    }

    @Test
    public void registerSuccess() {
        when(userService.isLogged()).thenReturn(false);
        try (MockedStatic<ConsoleReader> mockedReader = mockStatic(ConsoleReader.class)) {
            mockedReader.when(() -> ConsoleReader.readString("Логин")).thenReturn("newUser");
            mockedReader.when(() -> ConsoleReader.readString("Пароль")).thenReturn("password123");

            authController.handleCommand(List.of("reg"));

            ArgumentCaptor<RegistrationRequest> requestCaptor = ArgumentCaptor.forClass(RegistrationRequest.class);
            verify(userService, times(1)).register(requestCaptor.capture());

            RegistrationRequest request = requestCaptor.getValue();
            assertEquals("newUser", request.login());
            assertEquals("password123", request.password());

            verify(view, times(1)).printRegSuccess();
        }
    }

    @Test
    public void registerWhenAlreadyLogged() {
        when(userService.isLogged()).thenReturn(true);
        try (MockedStatic<ConsoleReader> mockedReader = mockStatic(ConsoleReader.class)) {
            authController.handleCommand(List.of("reg"));

            verify(view, times(1)).printAlreadyLogged();
            verify(userService, never()).register(any(RegistrationRequest.class));
            mockedReader.verifyNoInteractions();
        }
    }

    @Test
    public void registerException() {
        when(userService.isLogged()).thenReturn(false);
        PersonalFinanceException exception = new PersonalFinanceException("newUser");
        doThrow(exception).when(userService).register(any(RegistrationRequest.class));

        try (MockedStatic<ConsoleReader> mockedReader = mockStatic(ConsoleReader.class)) {
            mockedReader.when(() -> ConsoleReader.readString("Логин")).thenReturn("newUser");
            mockedReader.when(() -> ConsoleReader.readString("Пароль")).thenReturn("password123");

            authController.handleCommand(List.of("reg"));

            verify(view, times(1)).printError(exception);
            verify(view, never()).printRegSuccess();
        }
    }

    @Test
    public void logoutSuccess() {
        authController.handleCommand(List.of("logout"));

        verify(userService, times(1)).logout();
        verify(view, times(1)).printLogoutSuccess();
    }
}
