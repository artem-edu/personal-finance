package mephi.controller;

import mephi.core.Controller;
import mephi.dto.request.CreateCategory;
import mephi.dto.request.EditCategory;
import mephi.model.Category;
import mephi.model.User;
import mephi.service.CategoryService;
import mephi.service.UserService;
import mephi.util.ConsoleReader;
import mephi.view.CategoryView;

import java.math.BigDecimal;
import java.util.List;

public class CategoryController implements Controller {
    private final CategoryService categoryService;
    private final UserService userService;
    private final CategoryView view;

    public CategoryController(
            CategoryService categoryService,
            UserService userService,
            CategoryView view
    ) {
        this.categoryService = categoryService;
        this.userService = userService;
        this.view = view;
    }

    @Override
    public void help() {
        view.printHelp();
    }

    @Override
    public void handleCommand(List<String> args) {
        if (args.isEmpty()) {
            help();
            return;
        }
        String action = args.get(0);

        switch (action) {
            case "help" -> help();
            case "create" -> create();
            case "edit" -> edit();
            case "list" -> list();
            default -> view.printUnknownAction();
        }
    }

    private void create() {
        User user = userService.getCurrentUser();
        view.printCreateCategoryHeader();
        String name = ConsoleReader.readString("Название");
        boolean isDepositOnly = ConsoleReader.readBoolean("Режим только пополнения?");
        BigDecimal budget = BigDecimal.ZERO;

        if (!isDepositOnly) {
            int budgetValue = ConsoleReader.readInt("Бюджет");
            budget = BigDecimal.valueOf(budgetValue);
        }
        categoryService.create(new CreateCategory(name, user.getId(), budget, isDepositOnly));
        view.createSuccess(name);
    }

    private void edit() {
        User user = userService.getCurrentUser();
        view.printEditHeader();
        List<Category> categories = categoryService.getCategoriesByUserId(user.getId());
        view.printCategoryList(categories);
        
        int categoryChoice = ConsoleReader.readInt("Категория", 1, categories.size());
        Category category = categories.get(categoryChoice - 1);
        
        String name = ConsoleReader.readString("Название");
        boolean isDepositOnly = ConsoleReader.readBoolean("Режим только пополнения?", category.getIsDepositOnly());
        BigDecimal budget = BigDecimal.valueOf(ConsoleReader.readInt("Бюджет"));

        categoryService.update(new EditCategory(category.getId(), name, budget, isDepositOnly));
        view.updateSuccess(name);
    }

    private void list() {
        User user = userService.getCurrentUser();
        view.printCategoriesListHeader();
        List<Category> categories = categoryService.getCategoriesByUserId(user.getId());
        view.printCategoriesList(categories);
    }
}
