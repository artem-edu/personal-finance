package mephi.core;

import mephi.controller.*;
import mephi.repository.CategoryRepository;
import mephi.repository.TransactionRepository;
import mephi.repository.UserRepository;
import mephi.repository.WalletRepository;
import mephi.service.CategoryService;
import mephi.service.TransactionService;
import mephi.service.UserService;
import mephi.service.WalletService;
import mephi.util.SessionManager;

public class AppBootstrap {
    public static Router bootstrap() {
        DIContainer container = new DIContainer();

        // регистрация репозиториев
        container.provide(
                UserRepository.class,
                CategoryRepository.class,
                WalletRepository.class,
                CategoryRepository.class,
                TransactionRepository.class
        );

        // регистрация сервисов
        container.provide(
                SessionManager.class,
                UserService.class,
                WalletService.class,
                CategoryService.class,
                TransactionService.class
        );

        // регистрация контроллеров
        container.provide(
                AuthController.class,
                WalletController.class,
                HelpController.class,
                CategoryController.class,
                TransactionController.class
        );

        Router router = new Router();
        // регистрация маршрутов
        router.register("help", container.get(HelpController.class));
        router.register("auth", container.get(AuthController.class));
        router.register("wallet", container.get(WalletController.class));
        router.register("category", container.get(CategoryController.class));
        router.register("transaction", container.get(TransactionController.class));

        return router;
    }
}
