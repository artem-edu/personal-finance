package mephi.dto.request;

import mephi.exception.PersonalFinanceException;

public record CreateReport<T>(T data, String name) {
    public CreateReport {
        if (name == null || name.isBlank()) {
            throw new PersonalFinanceException("Имя отчета не может быть пустым");
        }
    }
}
