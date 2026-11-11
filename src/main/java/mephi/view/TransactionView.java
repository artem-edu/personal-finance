package mephi.view;

import mephi.model.Category;
import mephi.model.enums.TransactionType;

import java.util.List;

public class TransactionView {
    public void printHelp() {
        System.out.println("\n[ Транзакции ]");
        String format = "\t %-25s - %s\n";
        System.out.printf(format, "transaction create", "создать транзакцию");
        System.out.printf(format, "transaction report", "выгрузить cvs отчет");
    }

    public void printUnknownAction() {
        System.out.println("Команда не найдена, вывод справки >> transaction help");
    }

    public void printCreateHeder() {
        System.out.println("\n[ Создание транзакции ]");
    }

    public void printCategoryList(List<Category> categories) {
        System.out.printf("\n%d) %s\n", 0, "--- новая категория ---");
        for (int i = 0; i < categories.size(); i++) {
            System.out.printf("%d) %s\n", i + 1, categories.get(i).getName());
        }
    }

    public void printCreateCategoryPlaceholder() {
        System.out.println("\nСоздание категорий во время транзакции пока не доступно");
        System.out.println("Выполните команду >> category create");
    }

    public void printTransactions(TransactionType[] transactionTypes) {
        for (TransactionType type : transactionTypes) {
            System.out.printf("%d) %s\n", type.ordinal() + 1, type.getMessage());
        }
    }

    public void printTransactionSuccess() {
        System.out.println("\nТранзакция успешно выполнена");
    }

    public void printLimitExceededWarning(String categoryName) {
        System.out.printf("\nВнимание!\nПревышен бюджет по категории %s\n", categoryName);
    }
}
