package mephi.model;

import java.math.BigDecimal;
import java.util.List;

public record StatFull(List<StatByCategory> stats, StatShort total) {
    public BigDecimal income() {
        return total.stat().income();
    }

    public BigDecimal expenses() {
        return total.stat().expenses();
    }

    public BigDecimal currentBalance() {
        return total.currentBalance();
    }
}
