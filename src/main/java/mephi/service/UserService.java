package mephi.service;

import mephi.dto.request.LoginRequest;
import mephi.dto.request.RegistrationRequest;
import mephi.exception.AuthenticationException;
import mephi.exception.PersonalFinanceException;
import mephi.exception.UserAlreadyExistsException;
import mephi.model.User;
import mephi.model.Wallet;
import mephi.repository.UserRepository;
import mephi.repository.WalletRepository;
import mephi.util.PasswordEncoder;
import mephi.util.SessionManager;

import java.util.Optional;
import java.util.UUID;

public class UserService {
    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final SessionManager sessionManager;

    public UserService(
            UserRepository userRepository,
            WalletRepository walletRepository,
            SessionManager sessionManager
    ) {
        this.userRepository = userRepository;
        this.walletRepository = walletRepository;
        this.sessionManager = sessionManager;
    }

    public User register(RegistrationRequest dto) {
        if (existLogin(dto.login())) {
            throw new UserAlreadyExistsException(dto.login());
        }

        String hashPassword = PasswordEncoder.encode(dto.password());
        User user = new User(dto.login(), hashPassword);
        Wallet wallet = new Wallet(user.getId());

        userRepository.create(user);
        walletRepository.save(wallet);

        return user;
    }

    public UUID login(LoginRequest dto) {
        Optional<User> user = userRepository.findByLogin(dto.login());

        if (user.isEmpty() || !PasswordEncoder.match(dto.password(), user.get().getHasPassword())) {
            throw new AuthenticationException("Неверный логин или пароль");
        }

        return sessionManager.createSession(user.get().getId());
    }

    public void logout() {
        sessionManager.logout();
    }

    public boolean isLogged() {
        return sessionManager.isLogged();
    }

    public User getCurrentUser() {
        UUID userId = sessionManager.getUserId();

        return userRepository.findById(userId).orElseThrow(() -> {
            sessionManager.logout();
            return new PersonalFinanceException("Требуется авторизация");
        });
    }

    public Wallet getWallet() {
        UUID userId = sessionManager.getUserId();

        return walletRepository.findByUserId(userId)
                .orElseThrow(() -> new PersonalFinanceException("Кошелек не найден"));
    }

    private boolean existLogin(String login) {
        Optional<User> user = userRepository.findByLogin(login);

        return user.isPresent();
    }
}
