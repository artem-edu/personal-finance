package mephi.controller;

import mephi.core.Controller;
import mephi.dto.request.CreateCategory;
import mephi.model.Category;
import mephi.model.User;
import mephi.service.CategoryService;
import mephi.service.UserService;
import mephi.util.ConsoleReader;

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
        String action = args.getFirst();

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
        User user = userService.geCurrenttUser();
        System.out.println("\n=== Создание категории ===");
        String name = ConsoleReader.readString("Название");
        boolean isDepositOnly = ConsoleReader.readBoolean("Режим только пополнения?");
        categoryService.create(new CreateCategory(name, user.getId(), isDepositOnly));
        System.out.println(name + isDepositOnly);
    }

    private void list() {
        User user = userService.geCurrenttUser();
        System.out.println("\n=== Список категорий ===");

        List<Category> categories = categoryService.getCategoriesByUserId(user.getId());
        String headerFormat = "| %-15s | %-5s |%n";
        String rowFormat = "| %-15s | %-5s |%n";
        System.out.printf(headerFormat, "Имя", "Только для пополнения");

        System.out.println("+------+-----------------+---------------------------+");
        categories.forEach(category -> {
            System.out.printf(rowFormat, category.getName(), category.getIsDepositOnly());
        });
        System.out.println("+------+-----------------+---------------------------+");
    }
}
