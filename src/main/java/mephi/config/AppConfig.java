package mephi.config;

import mephi.repository.UserRepository;
import mephi.service.UserService;
import mephi.util.SessionManager;

public final class AppConfig {
    // utils
    private final SessionManager sessionManager;

    // Сервисы
    private final UserService userService;


    private AppConfig() {
        sessionManager = new SessionManager();

        userService = new UserService(new UserRepository(), sessionManager);
    }

    public static AppConfig create() {
        return new AppConfig();
    }

    public UserService getUserService() {
        return userService;
    }

    public SessionManager getSessionManager() {
        return sessionManager;
    }
}
