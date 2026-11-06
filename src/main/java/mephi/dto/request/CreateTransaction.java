package mephi.dto.request;

import mephi.model.enums.TransactionType;

import java.math.BigDecimal;
import java.util.UUID;

public record CreateTransaction(UUID categoryId, UUID walletId, TransactionType type, BigDecimal amount) {
}
