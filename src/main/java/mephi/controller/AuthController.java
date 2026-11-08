package mephi.controller;

import mephi.dto.request.LoginRequest;
import mephi.dto.request.RegistrationRequest;
import mephi.exception.PersonalFinanceException;
import mephi.service.UserService;
import mephi.util.ConsoleReader;
import mephi.core.Controller;
import mephi.view.AuthView;

import java.util.List;

public class AuthController implements Controller {
    private final UserService userService;
    private final AuthView view;

    public AuthController(
            UserService userService,
            AuthView view
    ) {
        this.userService = userService;
        this.view = view;
    }

    @Override
    public void help() {
        view.printHelp();
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
            default -> view.printUnknownAction();
        }
    }

    private void login() {
        if (userService.isLogged()) {
            view.printAlreadyLogged();
            return;
        }
        view.printLoginHeader();
        String login = ConsoleReader.readString("Логин");
        String password = ConsoleReader.readString("Пароль");

        try {
            userService.login(new LoginRequest(login, password));
            view.printLoginSuccess(login);

        } catch (PersonalFinanceException e) {
            view.printError(e);
        }
    }

    private void register() {
        if (userService.isLogged()) {
            view.printAlreadyLogged();
            return;
        }
        view.printRegHeader();
        String login = ConsoleReader.readString("Логин");
        String password = ConsoleReader.readString("Пароль");

        try {
            userService.register(new RegistrationRequest(login, password));
            view.printRegSuccess();
        } catch (PersonalFinanceException e) {
            view.printError(e);
        }
    }

    private void logout() {
        userService.logout();
        view.printLogoutSuccess();
    }
}
