package mephi.model.report;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvBindByPosition;
import com.opencsv.bean.CsvNumber;

import java.math.BigDecimal;

public class CategoriesStatReport {
    @CsvBindByPosition(position = 0)
    @CsvBindByName(column = "Категория")
    private String categoryName;

    @CsvBindByPosition(position = 1)
    @CsvBindByName(column = "Бюджет руб.")
    @CsvNumber(value = "0.00")
    private BigDecimal budget;

    @CsvBindByPosition(position = 2)
    @CsvBindByName(column = "Оставшийся бюджет руб.")
    @CsvNumber(value = "0.00")
    private BigDecimal remainingBudget;

    @CsvBindByPosition(position = 3)
    @CsvBindByName(column = "Доход руб.")
    @CsvNumber(value = "0.00")
    private BigDecimal income;

    @CsvBindByPosition(position = 4)
    @CsvBindByName(column = "Расход руб.")
    @CsvNumber(value = "0.00")
    private BigDecimal expenses;

    public CategoriesStatReport(
            String categoryName,
            BigDecimal budget,
            BigDecimal remainingBudget,
            BigDecimal income,
            BigDecimal expenses
    ) {
        this.categoryName = categoryName;
        this.budget = budget;
        this.remainingBudget = remainingBudget;
        this.income = income;
        this.expenses = expenses;
    }
}
