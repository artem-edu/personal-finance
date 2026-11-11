package mephi.repository;

import mephi.model.Category;
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

class CategoryRepositoryTest {
    private CategoryRepository categoryRepository;
    private Path tempDbFile;

    @BeforeEach
    void setUp(@TempDir Path tempDir) {
        tempDbFile = tempDir.resolve("test_categories.json");
        categoryRepository = new CategoryRepository(tempDbFile.toString());
    }

    @Test
    void saveAndFindByIdSuccess() {
        UUID walletId = UUID.randomUUID();
        Category category = new Category("Продукты", walletId, new BigDecimal("5000"), false);

        categoryRepository.save(category);
        Optional<Category> found = categoryRepository.findById(category.getId());

        assertTrue(found.isPresent());
        assertEquals(category.getId(), found.get().getId());
        assertEquals("Продукты", found.get().getName());
        assertTrue(Files.exists(tempDbFile));
    }

    @Test
    void findByIdReturnsEmpty() {
        Optional<Category> found = categoryRepository.findById(UUID.randomUUID());

        assertTrue(found.isEmpty());
    }

    @Test
    void findByWalletAndNameSuccess() {
        UUID walletId = UUID.randomUUID();
        Category category = new Category("Транспорт", walletId, new BigDecimal("1000"), false);
        categoryRepository.save(category);

        Optional<Category> found = categoryRepository.findByWalletAndName(walletId, "  транспорт  ");

        assertTrue(found.isPresent());
        assertEquals(category.getId(), found.get().getId());
    }

    @Test
    void findByWalletAndNameReturnsEmpty() {
        UUID walletId = UUID.randomUUID();
        Category category = new Category("Транспорт", walletId, new BigDecimal("1000"), false);
        categoryRepository.save(category);

        assertTrue(categoryRepository.findByWalletAndName(UUID.randomUUID(), "Транспорт").isEmpty());
        assertTrue(categoryRepository.findByWalletAndName(walletId, "Еда").isEmpty());
    }

    @Test
    void findByWalletSuccess() {
        UUID wallet1 = UUID.randomUUID();
        UUID wallet2 = UUID.randomUUID();

        Category cat1 = new Category("Еда", wallet1, new BigDecimal("3000"), false);
        Category cat2 = new Category("Спорт", wallet1, new BigDecimal("2000"), false);
        Category cat3 = new Category("Зарплата", wallet2, BigDecimal.ZERO, true);

        categoryRepository.save(cat1);
        categoryRepository.save(cat2);
        categoryRepository.save(cat3);

        List<Category> wallet1Categories = categoryRepository.findByWallet(wallet1);

        assertEquals(2, wallet1Categories.size());
        assertTrue(wallet1Categories.stream().allMatch(c -> c.getWalletId().equals(wallet1)));
    }

    @Test
    void findByWalletReturnsEmpty() {
        List<Category> result = categoryRepository.findByWallet(null);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void findAllSuccess() {
        Category cat1 = new Category("Еда", UUID.randomUUID(), new BigDecimal("3000"), false);
        Category cat2 = new Category("Спорт", UUID.randomUUID(), new BigDecimal("2000"), false);

        categoryRepository.save(cat1);
        categoryRepository.save(cat2);

        List<Category> all = categoryRepository.findAll();

        assertEquals(2, all.size());
    }
}
