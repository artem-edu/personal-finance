package mephi.dto.request;

import mephi.model.Transaction;

public record TransactionResult(
        Transaction transaction,
        boolean isLimitExceeded
) {
}
