package mephi.dto.request;

import mephi.exception.PersonalFinanceException;

public record RegistrationRequest(String login, String password) {
    public RegistrationRequest {
        if (login == null || login.isBlank()) {
            throw new PersonalFinanceException("Логин не может быть пустым");
        }

        if (login.length() < 3) {
            throw new PersonalFinanceException("Минимальная длина логина 3 символа");
        }

        if (password == null || password.isBlank()) {
            throw new PersonalFinanceException("Пароль не может быть пустым");
        }

        if (password.length() < 6) {
            throw new PersonalFinanceException("Минимальная длина пароля 6 символов");
        }
    }
}
