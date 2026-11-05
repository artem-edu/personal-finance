package mephi.core;

import mephi.controller.AuthController;
import mephi.controller.HelpController;
import mephi.controller.WalletController;
import mephi.repository.CategoryRepository;
import mephi.repository.UserRepository;
import mephi.repository.WalletRepository;
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
                WalletRepository.class
        );

        // регистрация сервисов
        container.provide(
                SessionManager.class,
                UserService.class,
                WalletService.class
        );

        // регистрация контроллеров
        container.provide(
                AuthController.class,
                WalletController.class,
                HelpController.class
        );

        Router router = new Router();
        // регистрация маршрутов
        router.register("help", container.get(HelpController.class));
        router.register("auth", container.get(AuthController.class));
        router.register("wallet", container.get(WalletController.class));

        return router;
    }
}
