package mephi.controller;

import mephi.config.AppConfig;
import mephi.util.ConsoleReader;

import java.util.UUID;

public class MainController {
    private final AppConfig config;

    public MainController(AppConfig config) {
        this.config = config;
    }

    public void run() {
        AuthController authController = new AuthController(config.getUserService());

        UUID token = authController.run();
        if (token == null) {
            System.out.println("До свидания.");
            return;
        }

        mainMenu(token);
    }

    private void mainMenu(UUID token) {
        boolean running = true;
        while (running) {
            System.out.println("\n=== ГЛАВНОЕ МЕНЮ ===");
            System.out.println("1) Кошелёк");
            System.out.println("2) Категории");
            System.out.println("3) Операции");
            System.out.println("4) Бюджеты");
            System.out.println("5) Статистика");
            System.out.println("9) Выйти из аккаунта");
            System.out.println("0) Завершить работу");
            System.out.print("> ");

            try {
                switch (ConsoleReader.readInt()) {
                    case 1, 2, 3, 4, 5 ->
                            System.out.println("Раздел ещё не реализован.");
                    case 9 -> {
                        config.getUserService().logout(token);
                        System.out.println("Вы вышли из аккаунта.");
                        run();               // снова экран входа
                        running = false;
                    }
                    case 0 -> running = false;
                    default -> System.out.println("Неизвестная команда");
                }
            } catch (RuntimeException e) {
                System.out.println("Ошибка: " + e.getMessage());
            }
        }
        System.out.println("До свидания.");
    }
}
