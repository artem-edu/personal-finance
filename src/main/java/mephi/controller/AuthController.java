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
        System.out.println("\n[ Авторизация и регистрация ]");
        String format = "\t %-25s - %s\n";
        System.out.printf(format, "auth login", "авторизация");
        System.out.printf(format, "auth reg", "регистрация");
        System.out.printf(format, "auth logout", "выход из учетной записи");
    }

    @Override
    public void handleCommand(List<String> args) {
        if (args.isEmpty()) {
            help();
            return;
        }
        String action = args.get(0);

        switch (action) {
            case "help" -> help();
            case "login" -> login();
            case "reg" -> register();
            case "logout" -> logout();
            default -> System.out.println("Команда не найдена, вывод справки >> auth help");
        }
    }

    private void login() {
        if (userService.isLogged()) {
            System.out.println("Вы уже авторизованы.");
            System.out.println("Для входа в другой аккаунт выполните выход >> auth logout");
            return;
        }
        System.out.println("\n[ Авторизация ]");
        String login = ConsoleReader.readString("Логин");
        String password = ConsoleReader.readString("Пароль");

        try {
            userService.login(new LoginRequest(login, password));
            System.out.println("\nДобро пожаловать, " + login);

        } catch (PersonalFinanceException e) {
            System.out.println("\nОшибка: " + e.getMessage());
        }
    }

    private void register() {
        if (userService.isLogged()) {
            System.out.println("\nВы уже авторизованы.");
            System.out.println("Для входа в другой аккаунт выполните выход >> auth logout");
            return;
        }
        System.out.println("\n[ Регистрация ]");
        String login = ConsoleReader.readString("Логин");
        String password = ConsoleReader.readString("Пароль");

        try {
            userService.register(new RegistrationRequest(login, password));
            System.out.println("\nПользователь создан. Теперь войдите. >> auth login");
        } catch (PersonalFinanceException e) {
            System.out.println("Ошибка регистрации: " + e.getMessage());
        }
    }

    private void logout() {
        userService.logout();
        System.out.println("\nВы вышли из системы.");
    }
}
