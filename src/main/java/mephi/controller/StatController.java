package mephi.controller;

import mephi.core.Controller;
import mephi.model.StatFull;
import mephi.model.StatShort;
import mephi.service.StatService;

import java.math.BigDecimal;
import java.util.List;

public class StatController implements Controller {
    private final StatService statService;

    public StatController(StatService statService) {
        this.statService = statService;
    }

    @Override
    public void help() {
        System.out.println("\n=== Статистика ===");
        System.out.println("stat short - получить краткую статистику");
        System.out.println("stat full - получить полную статистику");
    }

    @Override
    public void handleCommand(List<String> args) {
        if (args.isEmpty()) {
            help();
            return;
        }

        String action = args.get(0).toLowerCase();
        switch (action) {
            case "help" -> {
                help();
            }
            case "short" -> {
                showShort();
            }
            case "full" -> {
                showFull();
            }
        }
    }

    private void showShort() {
        StatShort statShort = statService.getShort();

        System.out.printf("Доходы: %,.2f руб.\n", statShort.stat().income());
        System.out.printf("Расходы: %,.2f руб.\n", statShort.stat().expenses());
        System.out.printf("Текущий баланс: %,.2f руб.\n", statShort.currentBalance());
    }

    private void showFull() {
        StatFull statFull = statService.getFull();
        statFull.stats().forEach(s -> {
            System.out.printf(
                    "Категория: %s.; Бюджет: %,.2f; Только пополнение: %b; Доход: %,.2f; Расход: %,.2f\n",
                    s.category().getName(),
                    s.category().getBudget(),
                    s.category().getIsDepositOnly(),
                    s.stat().income(),
                    s.stat().expenses()
            );
            s.transactions().forEach(t -> {
                System.out.printf(
                        "%10s Тип: %s.; Сумма: %,.2f;\n",
                        "",
                        t.getType().getMessage(),
                        t.getAmount()
                );
            });
        });
        System.out.printf("Доходы: %,.2f руб.\n", statFull.income());
        System.out.printf("Расходы: %,.2f руб.\n", statFull.expenses());
        System.out.printf("Текущий баланс: %,.2f руб.\n", statFull.currentBalance());
    }
}
