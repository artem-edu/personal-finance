package mephi.controller;

import mephi.core.Controller;
import mephi.model.*;
import mephi.service.CategoryService;
import mephi.service.ReportService;
import mephi.service.StatService;
import mephi.service.UserService;
import mephi.util.ConsoleReader;
import mephi.view.StatView;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class StatController implements Controller {
    private final StatService statService;
    private final CategoryService categoryService;
    private final UserService userService;
    private final StatView view;
    private final ReportService reportService;

    public StatController(
            StatService statService,
            CategoryService categoryService,
            UserService userService,
            StatView view,
            ReportService reportService
    ) {
        this.statService = statService;
        this.categoryService = categoryService;
        this.userService = userService;
        this.view = view;
        this.reportService = reportService;
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

        String action = args.get(0).toLowerCase();
        switch (action) {
            case "help" -> help();
            case "short" -> showShort();
            case "full" -> showFull();
            case "category" -> showByCategory();
            case "report" -> getReport();
            default -> view.printUnknownAction();
        }
    }

    private void showShort() {
        StatShort statShort = statService.getStatShort();
        view.printShort(statShort);
    }

    private void showFull() {
        Wallet wallet = userService.getWallet();
        StatFull statFull = statService.getStatFull();
        view.printFullStats(statFull, wallet.getBalance());
    }

    private void showByCategory() {
        Wallet wallet = userService.getWallet();
        List<Category> categories = categoryService.getCategoriesByUserId(wallet.getUserId());
        view.printCategoryList(categories);

        UUID[] categoriesIdChoice = Arrays.stream(ConsoleReader
                        .readIns("Категории (ввод через пробел)", 1, categories.size()))
                .mapToObj(i -> categories.get(i - 1).getId())
                .toArray(UUID[]::new);

        StatFull statFull = statService.getStatFullByCategories(categoriesIdChoice);
        view.printFullStats(statFull, wallet.getBalance());
    }

    private void getReport() {
        StatFull statFull = statService.getStatFull();
        reportService.byCategories(statFull.stats(), "test.cvs");
    }

}
