package mephi.service;

import mephi.dto.request.CreateCategory;
import mephi.exception.PersonalFinanceException;
import mephi.model.Category;
import mephi.model.Wallet;
import mephi.repository.CategoryRepository;
import mephi.repository.WalletRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
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
public class CategoryServiceTest {
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private  WalletRepository walletRepository;

    @InjectMocks
    private CategoryService categoryService;

    @Test
    public void createCategorySuccess() {
        UUID userId = UUID.randomUUID();
        CreateCategory dto = new CreateCategory("Продукты", userId, BigDecimal.valueOf(1000), false);
        Wallet mockWallet = new Wallet(userId);

        when(walletRepository.findByUserId(userId)).thenReturn(Optional.of(mockWallet));

        categoryService.create(dto);

        ArgumentCaptor<Category> categoryCaptor = ArgumentCaptor.forClass(Category.class);
        verify(categoryRepository).save(categoryCaptor.capture());

        Category savedCategory = categoryCaptor.getValue();
        assertEquals(dto.name(), savedCategory.getName());
        assertEquals(dto.budget(), savedCategory.getBudget());
        assertEquals(mockWallet.getId(), savedCategory.getWalletId());
        assertEquals(dto.isDepositOnly(), savedCategory.getIsDepositOnly());
    }

    @Test
    public void walletException() {
        UUID userId = UUID.randomUUID();
        CreateCategory dto = new CreateCategory("Продукты", userId, BigDecimal.valueOf(1000), false);
        when(walletRepository.findByUserId(userId)).thenReturn(Optional.empty());

        PersonalFinanceException ex = assertThrows(PersonalFinanceException.class, () -> {
            categoryService.create(dto);
        });

        assertEquals("Кошелек не найден", ex.getMessage());
        verifyNoInteractions(categoryRepository);
    }

    @Test
    public void categoryExistException() {
        UUID userId = UUID.randomUUID();
        Wallet mockWallet = new Wallet(userId);
        CreateCategory dto = new CreateCategory("Продукты", userId, BigDecimal.valueOf(1000), false);
        Category category = new Category("Продукты", userId, BigDecimal.valueOf(1000), false);
        when(walletRepository.findByUserId(userId)).thenReturn(Optional.of(mockWallet));
        when(categoryRepository.findByWalletAndName(mockWallet.getId(), "Продукты"))
                .thenReturn(Optional.of(category));

        PersonalFinanceException ex = assertThrows(PersonalFinanceException.class, () -> {
            categoryService.create(dto);
        });

        assertEquals("Категория уже существует", ex.getMessage());
        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    public void getCategoryByIdSuccess() {
        UUID walletId = UUID.randomUUID();
        UUID categoryId = UUID.randomUUID();
        Category mockCategory = new Category(
                categoryId,
                "Продукты",
                walletId,
                BigDecimal.valueOf(1000),
                false
        );
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(mockCategory));
        Category result = categoryService.getCategoryById(categoryId);

        assertNotNull(result);
        assertEquals(categoryId, result.getId());
        assertEquals("Продукты", result.getName());
        verify(categoryRepository, times(1)).findById(categoryId);
    }

    @Test
    public void getCategoryByIdException() {
        UUID categoryId = UUID.randomUUID();
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        PersonalFinanceException ex = assertThrows(PersonalFinanceException.class, () -> {
            categoryService.getCategoryById(categoryId);
        });

        assertEquals("Категория не найдена", ex.getMessage());
        verify(categoryRepository, times(1)).findById(categoryId);
        verifyNoMoreInteractions(categoryRepository);
    }

    @Test
    public void getCategoriesByUserIdSuccess() {
        UUID userId = UUID.randomUUID();
        UUID categoryId = UUID.randomUUID();
        Wallet mockWallet = new Wallet(userId);
        List<Category> mockCategories = List.of(
                new Category(
                        categoryId,
                        "Продукты",
                        mockWallet.getId(),
                        BigDecimal.valueOf(1000),
                        false
                ),
                new Category(
                        categoryId,
                        "Подписки",
                        mockWallet.getId(),
                        BigDecimal.valueOf(800),
                        false
                )
        );

        when(walletRepository.findByUserId(userId)).thenReturn(Optional.of(mockWallet));
        when(categoryRepository.findByWallet(mockWallet.getId())).thenReturn(mockCategories);

        List<Category> categories = categoryService.getCategoriesByUserId(userId);

        assertNotNull(categories);
        assertEquals(2, categories.size());
        assertEquals(mockCategories, categories);

        verify(walletRepository, times(1)).findByUserId(userId);
        verify(categoryRepository, times(1)).findByWallet(mockWallet.getId());
    }

    @Test
    public void getCategoriesByUserIdException() {
        UUID userId = UUID.randomUUID();
        when(walletRepository.findByUserId(userId)).thenReturn(Optional.empty());

        PersonalFinanceException ex = assertThrows(PersonalFinanceException.class, () -> {
            categoryService.getCategoriesByUserId(userId);
        });

        assertEquals("Кошелек не найден", ex.getMessage());
        verify(walletRepository, times(1)).findByUserId(userId);
        verifyNoMoreInteractions(walletRepository);
    }
}
