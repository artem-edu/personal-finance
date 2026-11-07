package mephi.service;

//Реализовать возможность отображения общей суммы доходов и расходов, а также данных по каждой категории.
//Выводить информацию о текущем состоянии бюджета для каждой категории, а также оставшийся лимит.
//Поддерживать вывод информации в терминал или в файл.

import mephi.model.*;
import mephi.model.enums.TransactionType;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class StatService {
    private final UserService userService;
    private final TransactionService transactionService;
    private final CategoryService categoryService;

    public StatService(
            UserService userService,
            TransactionService transactionService,
            CategoryService categoryService
    ) {
        this.userService = userService;
        this.transactionService = transactionService;
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
            statByCategories.add(
                    new StatByCategory(
                            category,
                            new Stat(getIncome(category.getId()), getExpenses(category.getId())),
                            getTransactionsByCategory(category.getId())
                    )
            );
        }

        return new StatFull(List.copyOf(statByCategories), getShort());
    }

    private BigDecimal getIncome() {
        return getTransactions()
                .stream()
                .filter(t -> t.getType() == TransactionType.DEPOSIT)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal getIncome(UUID categoryId) {
        return getTransactionsByCategory(categoryId)
                .stream()
                .filter(t -> t.getType() == TransactionType.DEPOSIT)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal getExpenses() {
        return getTransactions()
                .stream()
                .filter(t -> t.getType() == TransactionType.WITHDRAWAL)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal getExpenses(UUID categoryId) {
        return getTransactionsByCategory(categoryId)
                .stream()
                .filter(t -> t.getType() == TransactionType.WITHDRAWAL)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal getCurrentBalance() {
        Wallet wallet = userService.getWallet();
        return wallet.getBalance();
    }

    private List<Transaction> getTransactions() {
        Wallet wallet = userService.getWallet();
        return transactionService.getTransactionsByWalletId(wallet.getId());
    }

    private List<Transaction> getTransactionsByCategory(UUID categoryId) {
        return transactionService.getByCategoryId(categoryId);
    }
}
