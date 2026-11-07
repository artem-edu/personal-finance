package mephi.model.enums;

public enum TransactionType {
    WITHDRAWAL("Списание"),
    DEPOSIT("Пополнение");

    private final String message;
    private static final TransactionType[] VALUES = values();

    TransactionType(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public static int size() {
        return VALUES.length;
    }

    public static TransactionType get(int index) {
        if (index < 0 || index >= VALUES.length) {
            throw new IllegalArgumentException("Неверный индекс: " + index);
        }
        return VALUES[index];
    }
}
