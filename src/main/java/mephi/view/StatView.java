package mephi.view;

import com.github.freva.asciitable.AsciiTable;
import mephi.model.*;
import mephi.util.DateFormatter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class StatView {
    public void printHelp() {
        System.out.println("\n[ Статистика ]");
        String format = "\t %-25s - %s\n";
        System.out.printf(format, "stat short", "краткая статистика");
        System.out.printf(format, "stat full", "расширенная статистика по всем категориям");
        System.out.printf(format, "stat category", "расширенная статистика по выбранным категориям");
    }

    public void printShort(StatShort statShort) {
        System.out.println("[ Итого ]");
        System.out.printf("Доход: %,.2f руб.\n", statShort.stat().income());
        System.out.printf("Расходы: %,.2f руб.\n\n", statShort.stat().expenses());
        System.out.printf("Текущий баланс кошелька: %,.2f руб.\n", statShort.currentBalance());
    }

    public void printCategoryList(List<Category> categories) {
        System.out.println();
        for (int i = 0; i < categories.size(); i++) {
            System.out.printf("%d) %s\n", i + 1, categories.get(i).getName());
        }
    }

    public void printFullStats(StatFull statFull, BigDecimal currentBalance) {
        String[] categoriesHeaders = {"Категория", "Бюджет", "Оставшийся бюджет", "Только для пополнение", "Доход", "Расход", "Транзакции"};
        String[] transactionsHeader = {"Операция", "Сумма", "Дата"};
        List<String[]> categoriesRows = new ArrayList<>();

        statFull.stats().forEach(s -> {
            String renderedInnerTable = "-";
            if (!s.transactions().isEmpty()) {
                List<String[]> transactionsRows = new ArrayList<>();
                s.transactions().forEach(t -> transactionsRows.add(new String[]{
                        t.getType().getMessage(),
                        String.format("%,.2f руб", t.getAmount()),
                        DateFormatter.format(t.getCreatedAt())
                }));
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

        System.out.println();
        System.out.println(AsciiTable.getTable(categoriesHeaders, categoriesRows.toArray(new String[0][0])));
        
        printShort(new StatShort(statFull.total(), currentBalance));
    }

    public void printUnknownAction() {
        System.out.println("Команда не найдена, вывод справки >> stat help");
    }
}
