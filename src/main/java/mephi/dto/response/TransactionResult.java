package mephi.dto.response;

import mephi.model.Transaction;

public record TransactionResult(
        Transaction transaction,
        boolean isLimitExceeded
) {
}
