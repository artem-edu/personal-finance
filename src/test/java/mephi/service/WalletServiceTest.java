package mephi.service;

import mephi.exception.PersonalFinanceException;
import mephi.model.Wallet;
import mephi.repository.WalletRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class WalletServiceTest {
    @Mock
    private WalletRepository walletRepository;

    @InjectMocks
    private WalletService walletService;

    @Test
    void getBalanceSuccess() {
        UUID userId = UUID.randomUUID();
        Wallet wallet = mock(Wallet.class);
        BigDecimal expectedBalance = new BigDecimal("250.50");

        when(walletRepository.findByUserId(userId)).thenReturn(Optional.of(wallet));
        when(wallet.getBalance()).thenReturn(expectedBalance);

        BigDecimal actualBalance = walletService.getBalance(userId);

        assertEquals(expectedBalance, actualBalance);
        verify(walletRepository, times(1)).findByUserId(userId);
        verify(wallet, times(1)).getBalance();
    }

    @Test
    void getBalanceException() {
        UUID userId = UUID.randomUUID();
        when(walletRepository.findByUserId(userId)).thenReturn(Optional.empty());

        PersonalFinanceException exception = assertThrows(PersonalFinanceException.class, () -> {
            walletService.getBalance(userId);
        });

        assertEquals("Кошелек не найден", exception.getMessage());
        verify(walletRepository, times(1)).findByUserId(userId);
    }

}
