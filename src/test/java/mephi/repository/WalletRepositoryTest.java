package mephi.repository;

import mephi.model.Wallet;
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

class WalletRepositoryTest {
    private WalletRepository walletRepository;
    private Path tempDbFile;

    @BeforeEach
    void setUp(@TempDir Path tempDir) {
        tempDbFile = tempDir.resolve("test_wallet.json");
        walletRepository = new WalletRepository(tempDbFile.toString());
    }

    @Test
    void saveAndFindByIdSuccess() {
        UUID userId = UUID.randomUUID();
        Wallet wallet = new Wallet(userId);

        walletRepository.save(wallet);
        Optional<Wallet> found = walletRepository.findById(wallet.getId());

        assertTrue(found.isPresent());
        assertEquals(wallet.getId(), found.get().getId());
        assertEquals(userId, found.get().getUserId());
        assertTrue(Files.exists(tempDbFile));
    }

    @Test
    void updateSuccess() {
        UUID userId = UUID.randomUUID();
        Wallet wallet = new Wallet(userId);
        walletRepository.save(wallet);

        wallet.deposit(new BigDecimal("500.00"));
        walletRepository.update(wallet);

        Optional<Wallet> updated = walletRepository.findById(wallet.getId());
        assertTrue(updated.isPresent());
        assertEquals(new BigDecimal("500.00"), updated.get().getBalance());
    }

    @Test
    void findByIdReturnsEmpty() {
        Optional<Wallet> found = walletRepository.findById(UUID.randomUUID());

        assertTrue(found.isEmpty());
    }

    @Test
    void findByUserIdSuccess() {
        UUID userId = UUID.randomUUID();
        Wallet wallet = new Wallet(userId);
        walletRepository.save(wallet);

        Optional<Wallet> found = walletRepository.findByUserId(userId);

        assertTrue(found.isPresent());
        assertEquals(wallet.getId(), found.get().getId());
    }

    @Test
    void findByUserIdReturnsEmpty() {
        Optional<Wallet> found = walletRepository.findByUserId(UUID.randomUUID());

        assertTrue(found.isEmpty());
    }

    @Test
    void findAllSuccess() {
        Wallet wallet1 = new Wallet(UUID.randomUUID());
        Wallet wallet2 = new Wallet(UUID.randomUUID());

        walletRepository.save(wallet1);
        walletRepository.save(wallet2);

        List<Wallet> allWallets = walletRepository.findAll();

        assertEquals(2, allWallets.size());
    }

    @Test
    void afterLoadRestoresWalletIndexFromFile() {
        UUID userId = UUID.randomUUID();
        Wallet wallet = new Wallet(userId);
        walletRepository.save(wallet);

        WalletRepository reloadedRepository = new WalletRepository(tempDbFile.toString());
        Optional<Wallet> foundByUserId = reloadedRepository.findByUserId(userId);
        Optional<Wallet> foundById = reloadedRepository.findById(wallet.getId());

        assertTrue(foundByUserId.isPresent());
        assertEquals(wallet.getId(), foundByUserId.get().getId());
        assertTrue(foundById.isPresent());
    }
}
