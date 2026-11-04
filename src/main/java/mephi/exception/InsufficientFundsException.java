package mephi.exception;

import java.math.BigDecimal;

public class InsufficientFundsException extends PersonalFinanceException {
    public InsufficientFundsException(BigDecimal balance, BigDecimal required) {
        super("Недостаточно средств: баланс " + balance + ", необходимо " + required);
    }
}
