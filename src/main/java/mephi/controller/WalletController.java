package mephi.controller;

import mephi.model.User;
import mephi.service.UserService;
import mephi.service.WalletService;
import mephi.core.Controller;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class WalletController implements Controller {
    private final WalletService walletService;
    private final UserService userService;

    public WalletController(WalletService walletService, UserService userService) {
        this.walletService = walletService;
        this.userService = userService;
    }

    @Override
    public void help() {
        System.out.println("\n===Справка===");
        System.out.println("wallet balance - показать баланс");
    }

    @Override
    public void handleCommand(List<String> args) {
        User user = userService.getCurrentUser();
        if (args.isEmpty()) {
            help();
            return;
        }
        String action = args.get(0);
        switch (action) {
            case "help" -> {
                help();
            }
            case "balance" -> {
                showBalance(user.getId());
            }
            default -> {
                System.out.println("Команда не найдена, вывод справка wallet help");
            }
        }
    }

    private void showBalance(UUID userId) {
        BigDecimal balance = walletService.getBalance(userId);
        System.out.println("Баланс: " + balance + " руб.");
    }
}
