package mephi.model;

import java.math.BigDecimal;
import java.util.List;

public record StatByCategory(
        Category category,
        Stat stat,
        BigDecimal remainingBudget,
        List<Transaction> transactions
) {
}
