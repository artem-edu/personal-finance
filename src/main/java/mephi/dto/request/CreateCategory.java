package mephi.dto.request;

import java.math.BigDecimal;
import java.util.UUID;

public record CreateCategory(
        String name,
        UUID userId,
        BigDecimal budget,
        boolean isDepositOnly
) {
}
