package mephi.controller;

import mephi.dto.request.CreateTransaction;
import mephi.dto.response.TransactionResult;
import mephi.model.Category;
import mephi.model.Transaction;
import mephi.model.Wallet;
import mephi.model.enums.TransactionType;
import mephi.service.CategoryService;
import mephi.service.TransactionService;
import mephi.service.UserService;
import mephi.util.ConsoleReader;
import mephi.view.TransactionView;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TransactionControllerTest {
    @Mock
    private UserService userService;
    @Mock
    private TransactionService transactionService;
    @Mock
    private CategoryService categoryService;
    @Mock
    private TransactionView view;

    @InjectMocks
    private TransactionController transactionController;

    @Test
    public void helpEmptyArgs() {
        transactionController.handleCommand(List.of());

        verify(view, times(1)).printHelp();
    }

    @Test
    public void helpCommand() {
        transactionController.handleCommand(List.of("help"));

        verify(view, times(1)).printHelp();
    }

    @Test
    public void unknownAction() {
        transactionController.handleCommand(List.of("unknown"));

        verify(view, times(1)).printUnknownAction();
        verify(transactionService, never()).create(any(CreateTransaction.class));
    }

    @Test
    public void createSuccess() {
        UUID userId = UUID.randomUUID();
        Wallet wallet = new Wallet(userId);
        Category category = new Category("Продукты", wallet.getId(), BigDecimal.valueOf(1000), false);
        Transaction transaction = new Transaction(
                category.getId(),
                wallet.getId(),
                TransactionType.WITHDRAWAL,
                BigDecimal.valueOf(500)
        );
        when(userService.getWallet()).thenReturn(wallet);
        when(categoryService.getCategoriesByUserId(userId)).thenReturn(List.of(category));
        when(transactionService.create(any(CreateTransaction.class)))
                .thenReturn(new TransactionResult(transaction, false));

        try(MockedStatic<ConsoleReader> mockedReader = mockStatic(ConsoleReader.class)) {
            mockedReader.when(() -> ConsoleReader.readInt("Категория", 0, 1)).thenReturn(1);
            mockedReader.when(() -> ConsoleReader.readInt("Тип транзакции", 1, TransactionType.size())).thenReturn(1);
            mockedReader.when(() -> ConsoleReader.readInt("Сумма")).thenReturn(500);

            transactionController.handleCommand(List.of("create"));

            ArgumentCaptor<CreateTransaction> dtoCaptor = ArgumentCaptor.forClass(CreateTransaction.class);
            verify(transactionService, times(1)).create(dtoCaptor.capture());

            CreateTransaction dto = dtoCaptor.getValue();
            assertEquals(category.getId(), dto.categoryId());
            assertEquals(wallet.getId(), dto.walletId());
            assertEquals(TransactionType.WITHDRAWAL, dto.type());
            assertEquals(BigDecimal.valueOf(500), dto.amount());

            verify(view, times(1)).printCreateHeder();
            verify(view, times(1)).printCategoryList(List.of(category));
            verify(view, times(1)).printTransactions(TransactionType.values());
            verify(view, times(1)).printTransactionSuccess();
            verify(view, never()).printLimitExceededWarning(anyString());
        }
    }

    @Test
    public void createWithNewCategoryChoice() {
        UUID userId = UUID.randomUUID();
        Wallet wallet = new Wallet(userId);
        when(userService.getWallet()).thenReturn(wallet);
        when(categoryService.getCategoriesByUserId(userId)).thenReturn(List.of());

        try (MockedStatic<ConsoleReader> mockedReader = mockStatic(ConsoleReader.class)) {
            mockedReader.when(() -> ConsoleReader.readInt("Категория", 0, 0)).thenReturn(0);

            transactionController.handleCommand(List.of("create"));

            verify(view, times(1)).printCreateCategoryPlaceholder();
            verify(transactionService, never()).create(any(CreateTransaction.class));
            verify(view, never()).printTransactionSuccess();

            mockedReader.verify(() -> ConsoleReader.readInt("Тип транзакции", 1, TransactionType.size()), never());
            mockedReader.verify(() -> ConsoleReader.readInt("Сумма"), never());
        }
    }

    @Test
    public void createLimitExceeded() {
        UUID userId = UUID.randomUUID();
        Wallet wallet = new Wallet(userId);
        Category category = new Category("Продукты", wallet.getId(), BigDecimal.valueOf(1000), false);
        Transaction transaction = new Transaction(
                category.getId(),
                wallet.getId(),
                TransactionType.WITHDRAWAL,
                BigDecimal.valueOf(1500)
        );
        when(userService.getWallet()).thenReturn(wallet);
        when(categoryService.getCategoriesByUserId(userId)).thenReturn(List.of(category));
        when(transactionService.create(any(CreateTransaction.class)))
                .thenReturn(new TransactionResult(transaction, true));
        try(MockedStatic<ConsoleReader> mockedReader = mockStatic(ConsoleReader.class)) {
            mockedReader.when(() -> ConsoleReader.readInt("Категория", 0, 1)).thenReturn(1);
            mockedReader.when(() -> ConsoleReader.readInt("Тип транзакции", 1, TransactionType.size())).thenReturn(1);
            mockedReader.when(() -> ConsoleReader.readInt("Сумма")).thenReturn(1500);

            transactionController.handleCommand(List.of("create"));

            verify(view, times(1)).printTransactionSuccess();
            verify(view, times(1)).printLimitExceededWarning("Продукты");
        }
    }
}
