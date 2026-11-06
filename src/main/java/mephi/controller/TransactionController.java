package mephi.controller;

import mephi.core.Controller;
import mephi.dto.request.CreateTransaction;
import mephi.model.Category;
import mephi.model.Wallet;
import mephi.model.enums.TransactionType;
import mephi.service.CategoryService;
import mephi.service.TransactionService;
import mephi.service.UserService;
import mephi.util.ConsoleReader;

import java.math.BigDecimal;
import java.util.List;

public class TransactionController implements Controller {
    private final UserService userService;
    private final TransactionService transactionService;
    private final CategoryService categoryService;

    public TransactionController(
            UserService userService,
            TransactionService transactionService,
            CategoryService categoryService
    ) {
        this.userService = userService;
        this.transactionService = transactionService;
        this.categoryService = categoryService;
    }

    @Override
    public void help() {

    }

    @Override
    public void handleCommand(List<String> args) {
        Wallet wallet = userService.getWallet();
        if (args.isEmpty()) {
            help();
            return;
        }

        String action = args.getFirst();
        switch (action) {
            case "help" -> {
                help();
            }
            case "create" -> {
                create(wallet);
            }
        }
    }

    private void create(Wallet wallet) {
        System.out.println("\n=== Создание транзакции ===");
        List<Category> categories = categoryService.getCategoriesByUserId(wallet.getUserId());
        System.out.printf("%d) %s\n", 0, "--- новая категория ---");
        for (int i = 0; i < categories.size(); i++) {
            System.out.printf("%d) %s\n", i + 1, categories.get(i).getName());
        }
        int categoryChoice = ConsoleReader.readInt("Категория", 0, categories.size());

        if (categoryChoice == 0) {
            System.out.println("Создание категорий во время транзакции пока не доступно");
            System.out.println("Выполните команду >> category create");
            return;
        }
        Category category = categories.get(categoryChoice - 1);

        for (TransactionType type : TransactionType.values()) {
            System.out.printf("%d) %s\n", type.ordinal() + 1, type.getMessage());
        }
        int typeChoice = ConsoleReader.readInt("Тип транзакции", 1, TransactionType.size());

        BigDecimal amount;
        int amountInput = ConsoleReader.readInt("Сумма");
        amount = BigDecimal.valueOf(amountInput);

        transactionService.create(new CreateTransaction(
                        category.getId(),
                        wallet.getId(),
                        TransactionType.get(typeChoice -1),
                        amount
                )
        );
    }
}
