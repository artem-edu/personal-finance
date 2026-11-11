package mephi.service;

import mephi.dto.request.CreateTransaction;
import mephi.dto.response.TransactionResult;
import mephi.exception.PersonalFinanceException;
import mephi.model.Category;
import mephi.model.StatFullByCategory;
import mephi.model.Transaction;
import mephi.model.Wallet;
import mephi.model.enums.TransactionType;
import mephi.repository.CategoryRepository;
import mephi.repository.TransactionRepository;
import mephi.repository.WalletRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceTest {
    @Mock
    private TransactionRepository transactionRepository;
    @Mock
    private WalletRepository walletRepository;
    @Mock
    private StatService statService;
    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private TransactionService transactionService;

    @Test
    void createDepositSuccess() {
        UUID walletId = UUID.randomUUID();
        UUID categoryId = UUID.randomUUID();
        BigDecimal amount = new BigDecimal("100.00");
        CreateTransaction dto = new CreateTransaction(categoryId, walletId, TransactionType.DEPOSIT, amount);

        Wallet wallet = mock(Wallet.class);
        Category category = mock(Category.class);
        StatFullByCategory stat = mock(StatFullByCategory.class);

        when(walletRepository.findById(walletId)).thenReturn(Optional.of(wallet));
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(wallet.getId()).thenReturn(walletId);
        when(statService.getStatFullByCategory(categoryId)).thenReturn(stat);
        when(stat.remainingBudget()).thenReturn(new BigDecimal("500.00"));

        TransactionResult result = transactionService.create(dto);

        assertNotNull(result);
        assertFalse(result.isLimitExceeded());
        assertEquals(amount, result.transaction().getAmount());
        assertEquals(TransactionType.DEPOSIT, result.transaction().getType());

        verify(wallet, times(1)).deposit(amount);
        verify(wallet, never()).withdraw(any());
        verify(transactionRepository, times(1)).save(any(Transaction.class));
        verify(walletRepository, times(1)).update(wallet);
    }

    @Test
    void createWithdrawalSuccess() {
        UUID walletId = UUID.randomUUID();
        UUID categoryId = UUID.randomUUID();
        BigDecimal amount = new BigDecimal("50.00");
        CreateTransaction dto = new CreateTransaction(categoryId, walletId, TransactionType.WITHDRAWAL, amount);

        Wallet wallet = mock(Wallet.class);
        Category category = mock(Category.class);
        StatFullByCategory stat = mock(StatFullByCategory.class);

        when(walletRepository.findById(walletId)).thenReturn(Optional.of(wallet));
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(category.getIsDepositOnly()).thenReturn(false);
        when(wallet.getId()).thenReturn(walletId);
        when(statService.getStatFullByCategory(categoryId)).thenReturn(stat);
        when(stat.remainingBudget()).thenReturn(new BigDecimal("150.00"));

        TransactionResult result = transactionService.create(dto);

        assertNotNull(result);
        assertFalse(result.isLimitExceeded());

        verify(wallet, times(1)).withdraw(amount);
        verify(wallet, never()).deposit(any());
        verify(transactionRepository, times(1)).save(any(Transaction.class));
        verify(walletRepository, times(1)).update(wallet);
    }

    @Test
    void createWithdrawalSuccessLimitExceeded() {
        UUID walletId = UUID.randomUUID();
        UUID categoryId = UUID.randomUUID();
        BigDecimal amount = new BigDecimal("200.00");
        CreateTransaction dto = new CreateTransaction(categoryId, walletId, TransactionType.WITHDRAWAL, amount);

        Wallet wallet = mock(Wallet.class);
        Category category = mock(Category.class);
        StatFullByCategory stat = mock(StatFullByCategory.class);

        when(walletRepository.findById(walletId)).thenReturn(Optional.of(wallet));
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(category.getIsDepositOnly()).thenReturn(false);
        when(wallet.getId()).thenReturn(walletId);
        when(statService.getStatFullByCategory(categoryId)).thenReturn(stat);
        when(stat.remainingBudget()).thenReturn(new BigDecimal("-50.00"));

        TransactionResult result = transactionService.create(dto);

        assertNotNull(result);
        assertTrue(result.isLimitExceeded());

        verify(wallet, times(1)).withdraw(amount);
        verify(transactionRepository, times(1)).save(any(Transaction.class));
        verify(walletRepository, times(1)).update(wallet);
    }

    @Test
    void createTransactionWalletException() {
        UUID walletId = UUID.randomUUID();
        UUID categoryId = UUID.randomUUID();
        CreateTransaction dto = new CreateTransaction(categoryId, walletId, TransactionType.DEPOSIT, BigDecimal.TEN);

        when(walletRepository.findById(walletId)).thenReturn(Optional.empty());

        PersonalFinanceException exception = assertThrows(PersonalFinanceException.class, () -> {
            transactionService.create(dto);
        });

        assertEquals("Кошелек не найден", exception.getMessage());
        verifyNoInteractions(categoryRepository, transactionRepository, statService);
    }

    @Test
    void createCategoryException() {
        UUID walletId = UUID.randomUUID();
        UUID categoryId = UUID.randomUUID();
        CreateTransaction dto = new CreateTransaction(categoryId, walletId, TransactionType.DEPOSIT, BigDecimal.TEN);
        Wallet wallet = mock(Wallet.class);

        when(walletRepository.findById(walletId)).thenReturn(Optional.of(wallet));
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        PersonalFinanceException exception = assertThrows(PersonalFinanceException.class, () -> {
            transactionService.create(dto);
        });

        assertEquals("Категория не найдена", exception.getMessage());
        verifyNoInteractions(transactionRepository, statService);
        verify(walletRepository, never()).update(any());
    }

    @Test
    void createOnlyCategoryException() {
        UUID walletId = UUID.randomUUID();
        UUID categoryId = UUID.randomUUID();
        CreateTransaction dto = new CreateTransaction(categoryId, walletId, TransactionType.WITHDRAWAL, BigDecimal.TEN);

        Wallet wallet = mock(Wallet.class);
        Category category = mock(Category.class);

        when(walletRepository.findById(walletId)).thenReturn(Optional.of(wallet));
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(category.getIsDepositOnly()).thenReturn(true);
        when(category.getName()).thenReturn("Зарплата");

        PersonalFinanceException exception = assertThrows(PersonalFinanceException.class, () -> {
            transactionService.create(dto);
        });

        assertEquals("Нельзя списывать из 'Зарплата'.\nДоступна только для пополнения", exception.getMessage());
        verify(wallet, never()).withdraw(any());
        verifyNoInteractions(transactionRepository, statService);
        verify(walletRepository, never()).update(any());
    }

    @Test
    void getAllByWalletIdSuccess() {
        UUID walletId = UUID.randomUUID();
        Transaction transaction = mock(Transaction.class);
        List<Transaction> transactions = List.of(transaction);

        when(transactionRepository.findByWalletId(walletId)).thenReturn(transactions);

        List<Transaction> result = transactionService.getAllByWalletId(walletId);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(transaction, result.get(0));
        verify(transactionRepository, times(1)).findByWalletId(walletId);
    }
}
