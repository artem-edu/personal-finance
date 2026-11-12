package mephi.service;

import mephi.dto.request.CreateCategory;
import mephi.dto.request.EditCategory;
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

        Optional<Category> foundCategory = categoryRepository.findByWalletAndName(wallet.getId(), dto.name());

        if (foundCategory.isPresent()) {
            throw new PersonalFinanceException("Категория уже существует");
        }

        Category category = new Category(dto.name(), wallet.getId(), dto.budget(), dto.isDepositOnly());
        categoryRepository.save(category);
    }

    public void update(EditCategory dto) {
        Category category = getCategoryById(dto.categoryId());
        Optional<Category> foundCategory = categoryRepository.findByWalletAndName(category.getWalletId(), dto.name());

        if (foundCategory.isPresent() && foundCategory.get().getId().equals(dto.categoryId())) {
            throw new PersonalFinanceException(
                    String.format("Категория '%s' уже существует", dto.name())
            );
        }

        category.setName(dto.name());
        category.setBudget(dto.budget());
        category.setIsDepositOnly(dto.isDepositOnly());

        categoryRepository.save(category);
    }

    public Category getCategoryById(UUID categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new PersonalFinanceException("Категория не найдена"));
    }

    public List<Category> getCategoriesByUserId(UUID userId) {
        Wallet wallet = walletRepository.findByUserId(userId)
                .orElseThrow(() -> new PersonalFinanceException("Кошелек не найден"));

        return categoryRepository.findByWallet(wallet.getId());
    }
}
