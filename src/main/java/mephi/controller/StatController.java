package mephi.controller;

import com.github.freva.asciitable.AsciiTable;
import mephi.core.Controller;
import mephi.model.StatFull;
import mephi.model.StatShort;
import mephi.service.StatService;
import mephi.util.DateFormatter;

import java.util.ArrayList;
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
        System.out.printf("Общий доход: %,.2f руб.\n", statShort.stat().income());
        System.out.printf("Общие расходы: %,.2f руб.\n", statShort.stat().expenses());
        System.out.printf("Текущий баланс: %,.2f руб.\n", statShort.currentBalance());
    }

    private void showFull() {
        StatFull statFull = statService.getFull();
        String[] categoriesHeaders = {"Категория", "Бюджет", "Оставшийся бюджет", "Только для пополнение", "Доход", "Расход", "Транзакции"};
        String[] transactionsHeader = {"Операция", "Сумма", "Дата"};
        List<String[]> categoriesRows = new ArrayList<>();

        statFull.stats().forEach(s -> {
            String renderedInnerTable = "-";
            if (!s.transactions().isEmpty()) {
                List<String[]> transactionsRows = new ArrayList<>();
                s.transactions().forEach(t -> {
                    transactionsRows.add(new String[]{
                            t.getType().getMessage(),
                            String.format("%,.2f руб", t.getAmount()),
                            DateFormatter.format(t.getCreatedAt())
                    });
                });
                renderedInnerTable = AsciiTable.getTable(transactionsHeader, transactionsRows.toArray(new String[0][0]));
            }
            categoriesRows.add(new String[]{
                    s.category().getName(),
                    s.category().getIsDepositOnly() ? "-" : String.format("%,.2f руб.", s.category().getBudget()),
                    s.category().getIsDepositOnly() ? "-" : String.format("%,.2f руб", s.remainingBudget()),
                    s.category().getIsDepositOnly() ? "да" : "нет",
                    String.format("%,.2f руб.", s.stat().income()),
                    s.category().getIsDepositOnly() ? "-" : String.format("%,.2f", s.stat().expenses()),
                    renderedInnerTable
            });
        });

        System.out.println(AsciiTable.getTable(categoriesHeaders, categoriesRows.toArray(new String[0][0])));
        showShort();
    }
}
