package mephi.model;

import mephi.exception.PersonalFinanceException;

import java.math.BigDecimal;
import java.util.UUID;

public class Wallet {
    private final UUID id;
    private final UUID userId;
    private BigDecimal balance;

    public Wallet(UUID userId) {
        this.id = UUID.randomUUID();
        this.userId = userId;
        this.balance = BigDecimal.ZERO;
    }

    public Wallet(UUID id, UUID userId, BigDecimal balance) {
        this.id = id;
        this.userId = userId;
        this.balance = balance;
    }

    public void deposit(BigDecimal amount) {
        assertPositive(amount);
        balance = balance.add(amount);
    }

    public void withdraw(BigDecimal amount) {
        assertPositive(amount);
        // TODO: судя по условию задачи, можем уходить в минус, но с оповещением
//        if (balance.compareTo(amount) < 0) {
//            throw new InsufficientFundsException(balance, amount);
//        }
        balance = balance.subtract(amount);
    }

    public UUID getId() {
        return id;
    }

    public UUID getUserId() {
        return userId;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    private void assertPositive(BigDecimal amount) {
        if (amount == null || amount.signum() <= 0) {
            throw new PersonalFinanceException("Сумма должна быть > 0");
        }
    }
}
