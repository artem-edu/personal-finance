package mephi.model.report;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvBindByPosition;
import mephi.model.enums.TransactionType;
import mephi.util.DateFormatter;

import java.math.BigDecimal;

public class TransactionsStatReport {
    @CsvBindByName(column = "Категория")
    private String categoryName;

    @CsvBindByName(column = "Тип транзакции")
    private String transactionType;

    @CsvBindByName(column = "Сумма")
    private String amount;

    @CsvBindByName(column = "Дата")
    private String date;

    public TransactionsStatReport(
            String categoryName,
            TransactionType transactionType,
            BigDecimal amount,
            long date
    ) {
        this.categoryName = categoryName;
        this.transactionType = transactionType.getMessage();
        this.amount = String.format("%,.2f руб.", amount);
        this.date = DateFormatter.format(date);
    }
}
