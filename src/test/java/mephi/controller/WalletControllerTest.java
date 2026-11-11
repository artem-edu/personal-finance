package mephi.controller;

import mephi.model.User;
import mephi.service.UserService;
import mephi.service.WalletService;
import mephi.view.WalletView;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class WalletControllerTest {
    @Mock
    private WalletService walletService;
    @Mock
    private UserService userService;
    @Mock
    private WalletView view;

    @InjectMocks
    private WalletController walletController;

    @Test
    public void helpEmptyArgs() {
        walletController.handleCommand(List.of());

        verify(view, times(1)).printHelp();
    }

    @Test
    public void helpCommand() {
        walletController.handleCommand(List.of("help"));

        verify(view, times(1)).printHelp();
    }

    @Test
    public void unknownAction() {
        walletController.handleCommand(List.of("unknown"));

        verify(view, times(1)).printUnknownAction();
        verify(walletService, never()).getBalance(any(UUID.class));
    }

    @Test
    public void balanceSuccess() {
        UUID userId = UUID.randomUUID();
        User user = new User(userId, "testUser", "hashedPassword");
        BigDecimal balance = new BigDecimal("1500.00");
        when(userService.getCurrentUser()).thenReturn(user);
        when(walletService.getBalance(userId)).thenReturn(balance);

        walletController.handleCommand(List.of("balance"));

        verify(walletService, times(1)).getBalance(userId);
        verify(view, times(1)).printBalance(balance);
    }
}
