package mephi.controller;

import mephi.dto.request.LoginRequest;
import mephi.dto.request.RegistrationRequest;
import mephi.exception.PersonalFinanceException;
import mephi.service.UserService;
import mephi.util.ConsoleReader;
import mephi.core.Controller;

import java.util.List;

public class AuthController implements Controller {
    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void help() {
        System.out.println("\n=== Авторизация/регистрация ==");
        System.out.println("auth login - авторизация");
        System.out.println("auth reg - регистрация");
        System.out.println("auth logout - выход из учетной записи");
    }

    @Override
    public void handleCommand(List<String> args) {
        if (args.isEmpty()) {
            help();
            return;
        }
        String action = args.getFirst();

        switch (action) {
            case "help" -> {
                help();
            }
            case "login" -> {
                login();
            }
            case "reg" -> {
                register();
            }
            case "logout" -> {
                logout();
            }
            default -> {
                System.out.println("Команда не найдена, вывод справка auth help");
            }
        }
    }

    private void login() {
        if (userService.isLogged()) {
            System.out.println("Вы уже авторизованы.");
            System.out.println("Для входа в другой аккаунт выполните выход >> auth logout");
            return;
        }
        System.out.println("\n=== Авторизация ===");
        String login = ConsoleReader.readString("Логин: ");
        String password = ConsoleReader.readString("Пароль: ");

        try {
            userService.login(new LoginRequest(login, password));
            System.out.println("Добро пожаловать, " + login);

        } catch (PersonalFinanceException e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

    private void register() {
        if (userService.isLogged()) {
            System.out.println("Вы уже авторизованы.");
            System.out.println("Для входа в другой аккаунт выполните выход >> auth logout");
            return;
        }
        System.out.println("\n=== Регистрация ===");
        String login = ConsoleReader.readString("Логин: ");
        String password = ConsoleReader.readString("Пароль: ");

        try {
            userService.register(new RegistrationRequest(login, password));
            System.out.println("Пользователь создан. Теперь войдите. >> auth login");
        } catch (PersonalFinanceException e) {
            System.out.println("Ошибка регистрации: " + e.getMessage());
        }
    }

    private void logout() {
        userService.logout();
        System.out.println("Вы вышли из системы.");
    }
}
