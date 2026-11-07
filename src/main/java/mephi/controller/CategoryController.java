package mephi.controller;

import com.github.freva.asciitable.AsciiTable;
import mephi.core.Controller;
import mephi.dto.request.CreateCategory;
import mephi.model.Category;
import mephi.model.User;
import mephi.service.CategoryService;
import mephi.service.UserService;
import mephi.util.ConsoleReader;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class CategoryController implements Controller {
    private final CategoryService categoryService;
    private final UserService userService;

    public CategoryController(CategoryService categoryService, UserService userService) {
        this.categoryService = categoryService;
        this.userService = userService;
    }

    @Override
    public void help() {
        System.out.println("\n=== Категории ===");
        System.out.println("category create - создать категорию");
    }

    @Override
    public void handleCommand(List<String> args) {
        if (args.isEmpty()) {
            help();
            return;
        }
        String action = args.get(0);

        switch (action) {
            case "help" -> {
                help();
            }
            case "create" -> {
                create();
            }
            case "list" -> {
                list();
            }
        }
    }

    private void create() {
        User user = userService.getCurrentUser();
        System.out.println("\n=== Создание категории ===");
        String name = ConsoleReader.readString("Название");
        boolean isDepositOnly = ConsoleReader.readBoolean("Режим только пополнения?");
        BigDecimal budget = BigDecimal.ZERO;

        if (!isDepositOnly) {
            int budgetValue = ConsoleReader.readInt("Бюджет");
            budget = BigDecimal.valueOf(budgetValue);
        }
        categoryService.create(new CreateCategory(name, user.getId(), budget, isDepositOnly));
    }

    private void list() {
        User user = userService.getCurrentUser();
        System.out.println("\n=== Список категорий ===");
        String[] tableHeaders = {"Имя", "Бюджет", "Только пополнение"};
        List<String[]> tableRows = new ArrayList<>();
        List<Category> categories = categoryService.getCategoriesByUserId(user.getId());

        categories.forEach(category -> {
            String budgetWithCurrency = String.format("%,.2f руб.", category.getBudget());
            tableRows.add(new String[] {
                    category.getName(),
                    category.getIsDepositOnly() ? "-" : budgetWithCurrency,
                    category.getIsDepositOnly() ? "Да" : "Нет"
            });
        });
        System.out.println(AsciiTable.getTable(tableHeaders, tableRows.toArray(new String[0][0])));
    }
}
