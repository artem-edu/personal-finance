package mephi.controller;

import mephi.dto.request.LoginRequest;
import mephi.dto.request.RegistrationRequest;
import mephi.service.UserService;
import mephi.util.ConsoleReader;

import java.util.UUID;

public class AuthController {
    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    public UUID run() {
        while (true) {
            System.out.println("\n=== ВХОД ===");
            System.out.println("1) Войти");
            System.out.println("2) Зарегистрироваться");
            System.out.println("0) Выход");
            System.out.print("> ");

            switch (ConsoleReader.readInt()) {
                case 1 -> {
                    UUID token = login();
                    if (token != null) return token;
                }
                case 2 -> register();
                case 0 -> { return null; }
                default -> System.out.println("Неизвестная команда");
            }
        }
    }

    private UUID login() {
        String login = ConsoleReader.readString("Логин: ");
        String password = ConsoleReader.readString("Пароль: ");

        try {
            UUID token = userService.login(new LoginRequest(login, password));
            System.out.println("Добро пожаловать, " + login);

            return token;
        } catch (RuntimeException e) {
            System.out.println("Ошибка: " + e.getMessage());
            return null;
        }
    }

    private void register() {
        String login = ConsoleReader.readString("Логин: ");
        String password = ConsoleReader.readString("Пароль: ");

        try {
            userService.create(new RegistrationRequest(login, password));
            System.out.println("Пользователь создан. Теперь войдите.");
        } catch (RuntimeException e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }
}
