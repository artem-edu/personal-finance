package mephi.model;


import java.math.BigDecimal;
import java.util.UUID;

public class Category {
    private final UUID id;
    private String name;
    private final UUID walletId;
    private BigDecimal budget;
    private boolean isDepositOnly;

    public Category(String name, UUID walletId, BigDecimal budget, boolean isDepositOnly) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.walletId = walletId;
        this.budget = budget;
        this.isDepositOnly = isDepositOnly;
    }

    public Category(UUID id, String name, UUID walletId, BigDecimal budget, boolean isDepositOnly) {
        this.id = id;
        this.name = name;
        this.walletId = walletId;
        this.budget = budget;
        this.isDepositOnly = isDepositOnly;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public UUID getWalletId() {
        return walletId;
    }

    public BigDecimal getBudget() {
        return budget;
    }

    public boolean getIsDepositOnly() {
        return isDepositOnly;
    }

    public void setName(String name) {
        if (!name.isBlank()) {
            this.name = name;
        }
    }

    public void setBudget(BigDecimal budget) {
        if (budget != null) {
            this.budget = budget;
        }
    }

    public void setIsDepositOnly(boolean isDepositOnly) {
        this.isDepositOnly = isDepositOnly;
    }
}
