package mephi.controller;

import mephi.service.WalletService;
import mephi.util.ConsoleReader;
import mephi.util.SessionManager;

import java.math.BigDecimal;
import java.util.UUID;

public class WalletController {
    private final WalletService walletService;
    private final SessionManager sessionManager;

    public WalletController(WalletService walletService, SessionManager sessionManager) {
        this.walletService = walletService;
        this.sessionManager = sessionManager;
    }

    public void run(UUID token) {
        UUID userId = sessionManager.getUserId(token);
        while (true) {
            System.out.println("\n===Кошелек===");
            System.out.println("1) Баланс");
            System.out.println("0) Назад");
            System.out.print("> ");

            switch (ConsoleReader.readInt()) {
                case 1 -> {
                    showBalance(userId);
                }
                case 0 -> {
                    return;
                }
                default -> System.out.println("Неизвестная команда");
            }
        }
    }

    private void showBalance(UUID userId) {
        BigDecimal balance = walletService.getBalance(userId);
        System.out.println("Баланс: " + balance + " руб.");
    }
}
