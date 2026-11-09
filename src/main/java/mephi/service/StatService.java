package mephi.service;

import mephi.model.*;
import mephi.model.enums.TransactionType;
import mephi.repository.TransactionRepository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class StatService {
    private final UserService userService;
    private final TransactionRepository transactionRepository;
    private final CategoryService categoryService;

    public StatService(
            UserService userService,
            TransactionRepository transactionRepository,
            CategoryService categoryService
    ) {
        this.userService = userService;
        this.transactionRepository = transactionRepository;
        this.categoryService = categoryService;
    }

    public StatShort getStatShort() {
        return new StatShort(
                getStat(),
                getCurrentBalance()
        );
    }

    public Stat getStat() {
        return new Stat(getIncome(), getExpenses());
    }

    public StatFull getStatFull() {
        Wallet wallet = userService.getWallet();
        List<UUID> categoriesId = categoryService.getCategoriesByUserId(wallet.getUserId())
                .stream()
                .map(Category::getId)
                .toList();
        return buildStatFull(categoriesId);
    }

    public StatFull getStatFullByCategories(UUID[] categoriesId) {
        return buildStatFull(Arrays.asList(categoriesId));
    }

    public StatFullByCategory getStatFullByCategory(UUID categoryId) {
        Category category = categoryService.getCategoryById(categoryId);
        BigDecimal income = getIncome(categoryId);
        BigDecimal expenses = getExpenses(categoryId);
        BigDecimal remainingBudget = calculateRemainingBudget(category.getBudget(), expenses);

        List<Transaction> transactions = transactionRepository.findByCategoryId(categoryId);

        return new StatFullByCategory(
                category,
                new Stat(income, expenses),
                remainingBudget,
                transactions
        );
    }

    private StatFull buildStatFull(List<UUID> categoryIds) {
        List<StatFullByCategory> statByCategories = new ArrayList<>(categoryIds.size());
        BigDecimal income = BigDecimal.ZERO;
        BigDecimal expenses = BigDecimal.ZERO;

        for (UUID id : categoryIds) {
            income = income.add(getIncome(id));
            expenses = expenses.add(getExpenses(id));
            statByCategories.add(getStatFullByCategory(id));
        }

        return new StatFull(List.copyOf(statByCategories), new Stat(income, expenses));
    }

    private BigDecimal getIncome() {
        Wallet wallet = userService.getWallet();
        return transactionRepository.findAmountByWalletAndType(wallet.getId(), TransactionType.DEPOSIT);
    }

    private BigDecimal getIncome(UUID categoryId) {
        return transactionRepository.findAmountByCategoryAndType(categoryId, TransactionType.DEPOSIT);
    }

    private BigDecimal getExpenses() {
        Wallet wallet = userService.getWallet();
        return transactionRepository.findAmountByWalletAndType(wallet.getId(), TransactionType.WITHDRAWAL);
    }

    private BigDecimal getExpenses(UUID categoryId) {
        return transactionRepository.findAmountByCategoryAndType(categoryId, TransactionType.WITHDRAWAL);
    }

    private BigDecimal getCurrentBalance() {
        Wallet wallet = userService.getWallet();
        return wallet.getBalance();
    }

    private BigDecimal calculateRemainingBudget(BigDecimal budget, BigDecimal expenses) {
        if (budget == null || budget.signum() == 0) {
            return BigDecimal.ZERO;
        }
        return budget.subtract(expenses);
    }
}
