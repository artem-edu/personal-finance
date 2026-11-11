package mephi.exception;

public class UserAlreadyExistsException extends PersonalFinanceException {
    public UserAlreadyExistsException(String message) {
        super("Логин: " + message + " занят");
    }
}
