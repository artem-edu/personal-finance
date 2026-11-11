package mephi.model.report;

import com.opencsv.bean.CsvBindByName;

import java.math.BigDecimal;

public class CategoriesStatReport {
    @CsvBindByName(column = "Категория")
    private String categoryName;

    @CsvBindByName(column = "Бюджет")
    private String budget;

    @CsvBindByName(column = "Оставшийся бюджет руб.")
    private String remainingBudget;

    @CsvBindByName(column = "Доход")
    private String income;

    @CsvBindByName(column = "Расход")
    private String expenses;

    public CategoriesStatReport(
            String categoryName,
            BigDecimal budget,
            BigDecimal remainingBudget,
            BigDecimal income,
            BigDecimal expenses
    ) {
        this.categoryName = categoryName;
        this.budget = String.format("%,.2f руб.", budget);
        this.remainingBudget = String.format("%,.2f руб.", remainingBudget);;
        this.income = String.format("%,.2f руб.", income);
        this.expenses = String.format("%,.2f руб.", expenses);
    }
}
