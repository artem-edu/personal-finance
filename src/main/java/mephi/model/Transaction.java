package mephi.model;

import mephi.model.enums.TransactionType;

import java.math.BigDecimal;
import java.util.UUID;

public class Transaction {
    private final UUID id;
    private final UUID categoryId;
    private final UUID walletId;
    private final TransactionType type;
    private final BigDecimal amount;

    public Transaction(UUID categoryId, UUID walletId, TransactionType type, BigDecimal amount) {
        this.id = UUID.randomUUID();
        this.categoryId = categoryId;
        this.walletId = walletId;
        this.type = type;
        this.amount = amount;
    }

    public Transaction(UUID id, UUID categoryId, UUID walletId, TransactionType type, BigDecimal amount) {
        this.id = id;
        this.categoryId = categoryId;
        this.walletId = walletId;
        this.type = type;
        this.amount = amount;
    }

    public UUID getId() {
        return id;
    }

    public UUID getCategoryId() {
        return categoryId;
    }

    public UUID getWalletId() {
        return walletId;
    }

    public TransactionType getType() {
        return type;
    }

    public BigDecimal getAmount() {
        return amount;
    }
}
