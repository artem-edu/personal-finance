package mephi.repository;

import mephi.model.Transaction;
import mephi.model.enums.TransactionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class TransactionRepositoryTest {

    private TransactionRepository transactionRepository;
    private Path tempDbFile;

    @BeforeEach
    void setUp(@TempDir Path tempDir) {
        tempDbFile = tempDir.resolve("test_transactions.json");
        transactionRepository = new TransactionRepository(tempDbFile.toString());
    }

    @Test
    void saveAndFindByIdSuccess() {
        UUID categoryId = UUID.randomUUID();
        UUID walletId = UUID.randomUUID();
        Transaction transaction = new Transaction(categoryId, walletId, TransactionType.DEPOSIT, new BigDecimal("150.00"));

        transactionRepository.save(transaction);
        Optional<Transaction> found = transactionRepository.findById(transaction.getId());

        assertTrue(found.isPresent());
        assertEquals(transaction.getId(), found.get().getId());
        assertEquals(new BigDecimal("150.00"), found.get().getAmount());
        assertTrue(Files.exists(tempDbFile));
    }

    @Test
    void findByIdReturnsEmpty() {
        Optional<Transaction> found = transactionRepository.findById(UUID.randomUUID());

        assertTrue(found.isEmpty());
    }

    @Test
    void findByWalletIdSuccess() {
        UUID wallet1 = UUID.randomUUID();
        UUID wallet2 = UUID.randomUUID();
        UUID categoryId = UUID.randomUUID();

        Transaction t1 = new Transaction(categoryId, wallet1, TransactionType.DEPOSIT, new BigDecimal("100.00"));
        Transaction t2 = new Transaction(categoryId, wallet1, TransactionType.WITHDRAWAL, new BigDecimal("50.00"));
        Transaction t3 = new Transaction(categoryId, wallet2, TransactionType.DEPOSIT, new BigDecimal("200.00"));

        transactionRepository.save(t1);
        transactionRepository.save(t2);
        transactionRepository.save(t3);

        List<Transaction> wallet1Transactions = transactionRepository.findByWalletId(wallet1);

        assertEquals(2, wallet1Transactions.size());
        assertTrue(wallet1Transactions.stream().allMatch(t -> t.getWalletId().equals(wallet1)));
    }

    @Test
    void findByCategoryIdSuccess() {
        UUID walletId = UUID.randomUUID();
        UUID category1 = UUID.randomUUID();
        UUID category2 = UUID.randomUUID();

        Transaction t1 = new Transaction(category1, walletId, TransactionType.WITHDRAWAL, new BigDecimal("70.00"));
        Transaction t2 = new Transaction(category1, walletId, TransactionType.WITHDRAWAL, new BigDecimal("30.00"));
        Transaction t3 = new Transaction(category2, walletId, TransactionType.DEPOSIT, new BigDecimal("500.00"));

        transactionRepository.save(t1);
        transactionRepository.save(t2);
        transactionRepository.save(t3);

        List<Transaction> category1Transactions = transactionRepository.findByCategoryId(category1);

        assertEquals(2, category1Transactions.size());
        assertTrue(category1Transactions.stream().allMatch(t -> t.getCategoryId().equals(category1)));
    }

    @Test
    void findAmountByWalletAndTypeSuccess() {
        UUID walletId = UUID.randomUUID();
        UUID categoryId = UUID.randomUUID();

        transactionRepository.save(new Transaction(categoryId, walletId, TransactionType.DEPOSIT, new BigDecimal("100.50")));
        transactionRepository.save(new Transaction(categoryId, walletId, TransactionType.DEPOSIT, new BigDecimal("49.50")));
        transactionRepository.save(new Transaction(categoryId, walletId, TransactionType.WITHDRAWAL, new BigDecimal("30.00")));
        transactionRepository.save(new Transaction(categoryId, UUID.randomUUID(), TransactionType.DEPOSIT, new BigDecimal("999.00")));

        BigDecimal totalDeposits = transactionRepository.findAmountByWalletAndType(walletId, TransactionType.DEPOSIT);
        BigDecimal totalWithdrawals = transactionRepository.findAmountByWalletAndType(walletId, TransactionType.WITHDRAWAL);

        assertEquals(new BigDecimal("150.00"), totalDeposits);
        assertEquals(new BigDecimal("30.00"), totalWithdrawals);
    }

    @Test
    void findAmountByWalletAndTypeReturnsZero() {
        BigDecimal result = transactionRepository.findAmountByWalletAndType(UUID.randomUUID(), TransactionType.DEPOSIT);

        assertEquals(BigDecimal.ZERO, result);
    }

    @Test
    void findAmountByCategoryAndTypeSuccess() {
        UUID walletId = UUID.randomUUID();
        UUID categoryId = UUID.randomUUID();

        transactionRepository.save(new Transaction(categoryId, walletId, TransactionType.WITHDRAWAL, new BigDecimal("40.00")));
        transactionRepository.save(new Transaction(categoryId, walletId, TransactionType.WITHDRAWAL, new BigDecimal("60.25")));
        transactionRepository.save(new Transaction(categoryId, walletId, TransactionType.DEPOSIT, new BigDecimal("100.00")));
        transactionRepository.save(new Transaction(UUID.randomUUID(), walletId, TransactionType.WITHDRAWAL, new BigDecimal("500.00")));

        BigDecimal totalWithdrawals = transactionRepository.findAmountByCategoryAndType(categoryId, TransactionType.WITHDRAWAL);

        assertEquals(new BigDecimal("100.25"), totalWithdrawals);
    }

    @Test
    void findAllSuccess() {
        UUID walletId = UUID.randomUUID();
        UUID categoryId = UUID.randomUUID();

        transactionRepository.save(new Transaction(categoryId, walletId, TransactionType.DEPOSIT, BigDecimal.TEN));
        transactionRepository.save(new Transaction(categoryId, walletId, TransactionType.WITHDRAWAL, BigDecimal.ONE));

        List<Transaction> all = transactionRepository.findAll();

        assertEquals(2, all.size());
    }
}
