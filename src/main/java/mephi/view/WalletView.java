package mephi.view;

import java.math.BigDecimal;

public class WalletView {
    public void printHelp() {
        System.out.println("\n[ Кошелек ]");
        String format = "\t %-25s - %s\n";
        System.out.printf(format, "wallet balance", "показать баланс");
    }

    public void printUnknownAction() {
        System.out.println("Команда не найдена, вывод справки >> wallet help");
    }

    public void printBalance(BigDecimal balance) {
        System.out.printf("\nБаланс: %,.2f руб.", balance);
    }

}
