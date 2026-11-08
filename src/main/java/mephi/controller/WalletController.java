package mephi.controller;

import mephi.model.User;
import mephi.service.UserService;
import mephi.service.WalletService;
import mephi.core.Controller;
import mephi.view.WalletView;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class WalletController implements Controller {
    private final WalletService walletService;
    private final UserService userService;
    private final WalletView view;

    public WalletController(
            WalletService walletService,
            UserService userService,
            WalletView view
    ) {
        this.walletService = walletService;
        this.userService = userService;
        this.view = view;
    }

    @Override
    public void help() {
        view.printHelp();
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
            case "help" -> help();
            case "balance" -> showBalance(user.getId());
            default -> view.printUnknownAction();
        }
    }

    private void showBalance(UUID userId) {
        BigDecimal balance = walletService.getBalance(userId);
        view.printBalance(balance);
    }
}
