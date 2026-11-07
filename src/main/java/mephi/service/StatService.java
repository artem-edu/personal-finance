package mephi.service;

import mephi.model.*;
import mephi.model.enums.TransactionType;
import mephi.repository.TransactionRepository;

import java.math.BigDecimal;
import java.util.ArrayList;
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

    public StatShort getShort() {
        return new StatShort(
                new Stat(getIncome(), getExpenses()),
                getCurrentBalance()
        );
    }

    public StatFull getFull() {
        Wallet wallet = userService.getWallet();
        List<Category> categories = categoryService.getCategoriesByUserId(wallet.getUserId());
        ArrayList<StatByCategory> statByCategories = new ArrayList<>();

        for (Category category : categories) {
            statByCategories.add(getStatByCategory(category.getId()));
        }

        return new StatFull(List.copyOf(statByCategories), getShort());
    }

    public StatByCategory getStatByCategory(UUID categoryId) {
        Category category = categoryService.getCategoryById(categoryId);
        BigDecimal income = getIncome(categoryId);
        BigDecimal expenses = getExpenses(categoryId);
        BigDecimal remainingBudget = BigDecimal.ZERO;
        if (category.getBudget().signum() != 0) {
            remainingBudget = category.getBudget().subtract(expenses);
        }

        List<Transaction> transactions = transactionRepository.findByCategoryId(categoryId);

        return new StatByCategory(
                category,
                new Stat(income, expenses),
                remainingBudget,
                transactions
        );
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
}
