package mephi.controller;

import mephi.service.WalletService;
import mephi.core.Controller;
import mephi.util.SessionManager;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class WalletController implements Controller {
    private final WalletService walletService;
    private final SessionManager sessionManager;

    public WalletController(WalletService walletService, SessionManager sessionManager) {
        this.walletService = walletService;
        this.sessionManager = sessionManager;
    }

    @Override
    public void help() {
        System.out.println("\n===Справка===");
        System.out.println("wallet balance - показать баланс");
    }

    @Override
    public void handleCommand(List<String> args) {
        UUID userId = sessionManager.getUserId();
        if (args.isEmpty()) {
            help();
            return;
        }
        String action = args.getFirst();
        switch (action) {
            case "help" -> {
                help();
            }
            case "balance" -> {
                showBalance(userId);
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
