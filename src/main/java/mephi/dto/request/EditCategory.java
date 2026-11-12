package mephi.dto.request;

import mephi.exception.PersonalFinanceException;

import java.math.BigDecimal;
import java.util.UUID;

public record EditCategory(
        UUID categoryId,
        String name,
        BigDecimal budget,
        boolean isDepositOnly
) {
    public EditCategory {
        if (budget == null || budget.signum() < 0) {
            throw new PersonalFinanceException("Бюджет должен быть равен 0 и выше");
        }

        name = name.strip();
    }
}
