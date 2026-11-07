package mephi.dto.request;

import mephi.exception.PersonalFinanceException;

import java.math.BigDecimal;
import java.util.UUID;

public record CreateCategory(
        String name,
        UUID userId,
        BigDecimal budget,
        boolean isDepositOnly
) {
    public CreateCategory {
        if (name == null || name.isBlank()) {
            throw new PersonalFinanceException("Название категории не может быть пустым");
        }

        if (budget == null || budget.signum() < 0) {
            throw new PersonalFinanceException("Бюджет должен быть равен 0 и выше");
        }

        if (isDepositOnly && budget.signum() > 0) {
            throw new PersonalFinanceException("Для категорий типа 'Только пополнение' нельзя устанавливать бюджет");
        }

        name = name.strip();
    }
}
