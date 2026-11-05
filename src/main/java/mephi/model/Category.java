package mephi.model;


import java.util.UUID;

public class Category {
    private final UUID id;
    private final String name;
    private final UUID walletId;
    private final boolean isDepositOnly;

    public Category(String name, UUID walletId, boolean isDepositOnly) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.walletId = walletId;
        this.isDepositOnly = isDepositOnly;
    }

    private Category(UUID id, String name, UUID walletId, boolean isDepositOnly) {
        this.id = id;
        this.name = name;
        this.walletId = walletId;
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

    public boolean getIsDepositOnly() {
        return isDepositOnly;
    }
}
