package mephi.view;

import com.github.freva.asciitable.AsciiTable;
import mephi.model.Category;

import java.util.ArrayList;
import java.util.List;

public class CategoryView {
    public void printHelp() {
        System.out.println("\n[ Категории ]");
        String format = "\t %-25s - %s\n";
        System.out.printf(format, "category list", "список категорий");
        System.out.printf(format, "category create", "создать категорию");
        System.out.printf(format, "category edit", "редактировать категорию");
    }

    public void printUnknownAction() {
        System.out.println("\nКоманда не найдена, вывод справки >> category help");
    }

    public void printCreateCategoryHeader() {
        System.out.println("\n [Создание категории ]");
    }

    public void createSuccess(String name) {
        System.out.printf("\nКатегория %s успешно создана.\n", name);
    }

    public void printCategoriesListHeader() {
        System.out.println("\n[ Список категорий ]");
    }

    public void printCategoriesList(List<Category> categories) {
        String[] tableHeaders = {"Имя", "Бюджет", "Только пополнение"};
        List<String[]> tableRows = new ArrayList<>();

        categories.forEach(category -> {
            String budgetWithCurrency = String.format("%,.2f руб.", category.getBudget());
            tableRows.add(new String[]{
                    category.getName(),
                    category.getIsDepositOnly() ? "-" : budgetWithCurrency,
                    category.getIsDepositOnly() ? "Да" : "Нет"
            });
        });

        System.out.println();
        System.out.println(AsciiTable.getTable(tableHeaders, tableRows.toArray(new String[0][0])));
    }

    public void printEditHeader() {
        System.out.println("\n [Редактирование категории ]");
    }

    public void printCategoryList(List<Category> categories) {
        for (int i = 0; i < categories.size(); i++) {
            System.out.printf("%d) %s\n", i + 1, categories.get(i).getName());
        }
    }

    public void updateSuccess(String name) {
        System.out.printf("\nКатегория %s успешно сохранена.\n", name);
    }

}
