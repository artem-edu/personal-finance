package mephi.service;

import com.opencsv.CSVWriter;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import mephi.dto.request.CreateReport;
import mephi.exception.PersonalFinanceException;
import mephi.model.StatFullByCategory;
import mephi.model.report.CategoriesStatReport;

import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ReportService {
    public void byCategories(CreateReport<List<StatFullByCategory>> dto) {
        List<CategoriesStatReport> data = new ArrayList<>();

        dto.data().forEach(item -> {
            data.add(new CategoriesStatReport(
                    item.category().getName(),
                    item.category().getBudget(),
                    item.remainingBudget(),
                    item.stat().income(),
                    item.stat().expenses()
            ));
        });

        System.out.println(data.size());
        LocalDate now = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String dateStr = now.format(formatter);
        String reportName = String.format("%s_%s.csv", dto.name(), dateStr);


        try (FileOutputStream fos = new FileOutputStream(reportName);
             OutputStreamWriter osw = new OutputStreamWriter(fos, StandardCharsets.UTF_8);) {


            // Записываем сигнатуру BOM (Byte Order Mark) в самое начало файла.
            // Без этого русский Excel не поймет, что файл в UTF-8.
            fos.write(0xEF);
            fos.write(0xBB);
            fos.write(0xBF);

            StatefulBeanToCsv<CategoriesStatReport> beanToCsv = new StatefulBeanToCsvBuilder<CategoriesStatReport>(osw)
                    .withSeparator(';')
                    .withQuotechar(CSVWriter.DEFAULT_QUOTE_CHARACTER)
                    .withErrorLocale(new Locale("ru", "RU"))
                    .build();

            beanToCsv.write(data);
            System.out.println("Отчет успешно выгружен");
            System.out.println("Путь: ./" + reportName);

        } catch (Exception e) {
            throw new PersonalFinanceException("Ошибка выгрузки отчета:" + e.getMessage());
        }
    }
}
