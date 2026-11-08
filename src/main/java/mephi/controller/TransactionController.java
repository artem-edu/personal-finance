package mephi.controller;

import mephi.core.Controller;
import mephi.dto.request.CreateTransaction;
import mephi.dto.response.TransactionResult;
import mephi.model.Category;
import mephi.model.Wallet;
import mephi.model.enums.TransactionType;
import mephi.service.CategoryService;
import mephi.service.TransactionService;
import mephi.service.UserService;
import mephi.util.ConsoleReader;
import mephi.view.TransactionView;

import java.math.BigDecimal;
import java.util.List;

public class TransactionController implements Controller {
    private final UserService userService;
    private final TransactionService transactionService;
    private final CategoryService categoryService;
    private final TransactionView view;

    public TransactionController(
            UserService userService,
            TransactionService transactionService,
            CategoryService categoryService,
            TransactionView view
    ) {
        this.userService = userService;
        this.transactionService = transactionService;
        this.categoryService = categoryService;
        this.view = view;
    }

    @Override
    public void help() {
        view.printHelp();
    }

    @Override
    public void handleCommand(List<String> args) {
        Wallet wallet = userService.getWallet();
        if (args.isEmpty()) {
            help();
            return;
        }

        String action = args.get(0);
        switch (action) {
            case "help" -> help();
            case "create" -> create(wallet);
            default -> view.printUnknownAction();
        }
    }

    private void create(Wallet wallet) {
        view.printCreateHeder();
        List<Category> categories = categoryService.getCategoriesByUserId(wallet.getUserId());
        view.printCategoryList(categories);
        int categoryChoice = ConsoleReader.readInt("Категория", 0, categories.size());

        if (categoryChoice == 0) {
            view.printCreateCategoryPlaceholder();
            return;
        }
        Category category = categories.get(categoryChoice - 1);

        view.printTransactions(TransactionType.values());
        int typeChoice = ConsoleReader.readInt("Тип транзакции", 1, TransactionType.size());

        BigDecimal amount;
        int amountInput = ConsoleReader.readInt("Сумма");
        amount = BigDecimal.valueOf(amountInput);

        TransactionResult result = transactionService.create(new CreateTransaction(
                        category.getId(),
                        wallet.getId(),
                        TransactionType.get(typeChoice - 1),
                        amount
                )
        );
        view.printTransactionSuccess();
        if (result.isLimitExceeded()) {
            view.printLimitExceededWarning(category.getName());
        }
    }
}
