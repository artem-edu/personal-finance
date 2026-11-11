package mephi.service;

import mephi.dto.request.CreateReport;
import mephi.exception.PersonalFinanceException;
import mephi.model.Category;
import mephi.model.Stat;
import mephi.model.StatFullByCategory;
import mephi.model.Transaction;
import mephi.model.Wallet;
import mephi.model.enums.TransactionType;
import mephi.repository.CategoryRepository;
import mephi.util.DateFormatter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReportServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private ReportService reportService;

    private String reportFileName(String name) {
        String dateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        return String.format("%s_%s.csv", name, dateStr);
    }

    @Test
    void byCategoriesSuccess() {
        String name = "test_categories_report";
        String fileName = reportFileName(name);
        Wallet wallet = new Wallet(UUID.randomUUID());
        Category category = new Category("Продукты", wallet.getId(), new BigDecimal("1000.00"), false);
        Stat stat = new Stat(new BigDecimal("300.00"), new BigDecimal("150.00"));
        BigDecimal remainingBudget = new BigDecimal("850.00");
        StatFullByCategory statFull = new StatFullByCategory(category, stat, remainingBudget, List.of());

        try {
            reportService.byCategories(new CreateReport<>(List.of(statFull), name));

            File file = new File(fileName);
            assertTrue(file.exists());

            String content = Files.readString(file.toPath());
            assertTrue(content.contains("КАТЕГОРИЯ"));
            assertTrue(content.contains("Продукты"));
            assertTrue(content.contains(String.format("%,.2f руб.", category.getBudget())));
            assertTrue(content.contains(String.format("%,.2f руб.", remainingBudget)));
            assertTrue(content.contains(String.format("%,.2f руб.", stat.income())));
            assertTrue(content.contains(String.format("%,.2f руб.", stat.expenses())));
        } catch (Exception e) {
            fail(e);
        } finally {
            new File(fileName).delete();
        }
    }

    @Test
    void byTransactionsSuccess() {
        String name = "test_transactions_report";
        String fileName = reportFileName(name);
        Wallet wallet = new Wallet(UUID.randomUUID());
        Category category = new Category("Продукты", wallet.getId(), new BigDecimal("1000.00"), false);
        BigDecimal amount = new BigDecimal("500.00");
        Transaction transaction = new Transaction(
                category.getId(),
                wallet.getId(),
                TransactionType.WITHDRAWAL,
                amount
        );

        when(categoryRepository.findAll()).thenReturn(List.of(category));

        try {
            reportService.byTransactions(new CreateReport<>(List.of(transaction), name));

            File file = new File(fileName);
            assertTrue(file.exists());

            String content = Files.readString(file.toPath());
            assertTrue(content.contains("КАТЕГОРИЯ"));
            assertTrue(content.contains("ТИП ТРАНЗАКЦИИ"));
            assertTrue(content.contains("Продукты"));
            assertTrue(content.contains(TransactionType.WITHDRAWAL.getMessage()));
            assertTrue(content.contains(String.format("%,.2f руб.", amount)));
            assertTrue(content.contains(DateFormatter.format(transaction.getCreatedAt())));
        } catch (Exception e) {
            fail(e);
        } finally {
            new File(fileName).delete();
        }
    }

    @Test
    void byTransactionsCategoryNotFoundException() {
        String name = "test_transactions_report_not_found";
        Transaction transaction = new Transaction(
                UUID.randomUUID(),
                UUID.randomUUID(),
                TransactionType.DEPOSIT,
                BigDecimal.TEN
        );

        when(categoryRepository.findAll()).thenReturn(List.of());

        PersonalFinanceException exception = assertThrows(PersonalFinanceException.class, () -> {
            reportService.byTransactions(new CreateReport<>(List.of(transaction), name));
        });

        assertEquals("Категория не найдена", exception.getMessage());
        assertFalse(new File(reportFileName(name)).exists());
    }
}
