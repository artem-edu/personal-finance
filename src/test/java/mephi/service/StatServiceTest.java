package mephi.service;

import mephi.model.*;
import mephi.model.enums.TransactionType;
import mephi.repository.TransactionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StatServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private CategoryService categoryService;

    @InjectMocks
    private StatService statService;

    @Test
    void getStatShortSuccess() {
        Wallet wallet = mock(Wallet.class);
        BigDecimal income = new BigDecimal("100.00");
        BigDecimal expenses = new BigDecimal("50.00");
        BigDecimal balance = new BigDecimal("500.00");
        UUID walletId = UUID.randomUUID();

        when(userService.getWallet()).thenReturn(wallet);
        when(wallet.getId()).thenReturn(walletId);
        when(wallet.getBalance()).thenReturn(balance);
        when(transactionRepository.findAmountByWalletAndType(walletId, TransactionType.DEPOSIT)).thenReturn(income);
        when(transactionRepository.findAmountByWalletAndType(walletId, TransactionType.WITHDRAWAL)).thenReturn(expenses);

        StatShort result = statService.getStatShort();

        assertNotNull(result);
        assertEquals(balance, result.currentBalance());
        assertEquals(income, result.stat().income());
        assertEquals(expenses, result.stat().expenses());
    }

    @Test
    void getStatSuccess() {
        Wallet wallet = mock(Wallet.class);
        BigDecimal income = new BigDecimal("200.00");
        BigDecimal expenses = new BigDecimal("120.00");
        UUID walletId = UUID.randomUUID();

        when(userService.getWallet()).thenReturn(wallet);
        when(wallet.getId()).thenReturn(walletId);
        when(transactionRepository.findAmountByWalletAndType(walletId, TransactionType.DEPOSIT)).thenReturn(income);
        when(transactionRepository.findAmountByWalletAndType(walletId, TransactionType.WITHDRAWAL)).thenReturn(expenses);

        Stat result = statService.getStat();

        assertNotNull(result);
        assertEquals(income, result.income());
        assertEquals(expenses, result.expenses());
    }

    @Test
    void getStatFullSuccess() {
        UUID userId = UUID.randomUUID();
        UUID categoryId = UUID.randomUUID();

        Wallet wallet = mock(Wallet.class);
        Category category = mock(Category.class);
        Transaction transaction = mock(Transaction.class);

        when(userService.getWallet()).thenReturn(wallet);
        when(wallet.getUserId()).thenReturn(userId);
        when(categoryService.getCategoriesByUserId(userId)).thenReturn(List.of(category));
        when(category.getId()).thenReturn(categoryId);
        when(category.getBudget()).thenReturn(new BigDecimal("1000.00"));
        when(categoryService.getCategoryById(categoryId)).thenReturn(category);

        when(transactionRepository.findAmountByCategoryAndType(categoryId, TransactionType.DEPOSIT)).thenReturn(new BigDecimal("300.00"));
        when(transactionRepository.findAmountByCategoryAndType(categoryId, TransactionType.WITHDRAWAL)).thenReturn(new BigDecimal("150.00"));
        when(transactionRepository.findByCategoryId(categoryId)).thenReturn(List.of(transaction));

        StatFull result = statService.getStatFull();

        assertNotNull(result);
        assertEquals(1, result.stats().size());
        assertEquals(new BigDecimal("300.00"), result.total().income());
        assertEquals(new BigDecimal("150.00"), result.total().expenses());
    }

    @Test
    void getStatFullByCategoriesSuccess() {
        UUID categoryId = UUID.randomUUID();
        Category category = mock(Category.class);
        Transaction transaction = mock(Transaction.class);

        when(category.getBudget()).thenReturn(new BigDecimal("500.00"));
        when(categoryService.getCategoryById(categoryId)).thenReturn(category);
        when(transactionRepository.findAmountByCategoryAndType(categoryId, TransactionType.DEPOSIT)).thenReturn(new BigDecimal("50.00"));
        when(transactionRepository.findAmountByCategoryAndType(categoryId, TransactionType.WITHDRAWAL)).thenReturn(new BigDecimal("30.00"));
        when(transactionRepository.findByCategoryId(categoryId)).thenReturn(List.of(transaction));

        StatFull result = statService.getStatFullByCategories(new UUID[]{categoryId});

        assertNotNull(result);
        assertEquals(1, result.stats().size());
        assertEquals(new BigDecimal("50.00"), result.total().income());
        assertEquals(new BigDecimal("30.00"), result.total().expenses());
    }

    @Test
    void getStatFullByCategorySuccess() {
        UUID categoryId = UUID.randomUUID();
        Category category = mock(Category.class);
        Transaction transaction = mock(Transaction.class);

        when(categoryService.getCategoryById(categoryId)).thenReturn(category);
        when(category.getBudget()).thenReturn(new BigDecimal("1000.00"));
        when(transactionRepository.findAmountByCategoryAndType(categoryId, TransactionType.DEPOSIT)).thenReturn(new BigDecimal("400.00"));
        when(transactionRepository.findAmountByCategoryAndType(categoryId, TransactionType.WITHDRAWAL)).thenReturn(new BigDecimal("200.00"));
        when(transactionRepository.findByCategoryId(categoryId)).thenReturn(List.of(transaction));

        StatFullByCategory result = statService.getStatFullByCategory(categoryId);

        assertNotNull(result);
        assertEquals(category, result.category());
        assertEquals(new BigDecimal("400.00"), result.stat().income());
        assertEquals(new BigDecimal("200.00"), result.stat().expenses());
        assertEquals(new BigDecimal("800.00"), result.remainingBudget());
        assertEquals(1, result.transactions().size());
    }

    @Test
    void getStatFullByCategoryZeroBudget() {
        UUID categoryId = UUID.randomUUID();
        Category category = mock(Category.class);

        when(categoryService.getCategoryById(categoryId)).thenReturn(category);
        when(category.getBudget()).thenReturn(null);
        when(transactionRepository.findAmountByCategoryAndType(categoryId, TransactionType.DEPOSIT)).thenReturn(BigDecimal.ZERO);
        when(transactionRepository.findAmountByCategoryAndType(categoryId, TransactionType.WITHDRAWAL)).thenReturn(new BigDecimal("50.00"));
        when(transactionRepository.findByCategoryId(categoryId)).thenReturn(Collections.emptyList());

        StatFullByCategory result = statService.getStatFullByCategory(categoryId);

        assertNotNull(result);
        assertEquals(BigDecimal.ZERO, result.remainingBudget());
    }
}
