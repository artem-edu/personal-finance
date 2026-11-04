package mephi.config;

import mephi.repository.UserRepository;
import mephi.repository.WalletRepository;
import mephi.service.UserService;
import mephi.service.WalletService;
import mephi.util.SessionManager;

public final class AppConfig {
    // utils
    private final SessionManager sessionManager = new SessionManager();

    // Репозитории
    private final UserRepository userRepository = new UserRepository();
    private final WalletRepository walletRepository = new WalletRepository();

    // Сервисы
    private final UserService userService;
    private final WalletService walletService;


    private AppConfig() {
        userService = new UserService(userRepository, walletRepository, sessionManager);
        walletService = new WalletService(walletRepository);
    }

    public static AppConfig create() {
        return new AppConfig();
    }

    public UserService getUserService() {
        return userService;
    }

    public WalletService getWalletService() {
        return walletService;
    }

    public SessionManager getSessionManager() {
        return sessionManager;
    }
}
