package mephi.service;

import mephi.dto.request.CreateCategory;
import mephi.exception.PersonalFinanceException;
import mephi.model.Category;
import mephi.model.Wallet;
import mephi.repository.CategoryRepository;
import mephi.repository.WalletRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final WalletRepository walletRepository;

    public CategoryService(CategoryRepository categoryRepository, WalletRepository walletRepository) {
        this.categoryRepository = categoryRepository;
        this.walletRepository = walletRepository;
    }

    public void create(CreateCategory dto) {
        Wallet wallet = walletRepository.findByUserId(dto.userId())
                .orElseThrow(() -> new PersonalFinanceException("Кошелек не найден"));

        Category category = new Category(dto.name(), wallet.getId(), dto.budget(), dto.isDepositOnly());
        categoryRepository.save(category);
    }

    public Category getCategoriesByUserAndName(UUID userId, String name) {
        Wallet wallet = walletRepository.findByUserId(userId)
                .orElseThrow(() -> new PersonalFinanceException("Кошелек не найден"));

        Category category = categoryRepository.findByWalletAndName(wallet.getId(), name)
                .orElseThrow(() -> new PersonalFinanceException("Категория не найдена"));
        return category;
    }

    public List<Category> getCategoriesByUserId(UUID userId) {
        Wallet wallet = walletRepository.findByUserId(userId)
                .orElseThrow(() -> new PersonalFinanceException("Кошелек не найден"));

        return categoryRepository.findByWallet(wallet.getId());
    }
}
