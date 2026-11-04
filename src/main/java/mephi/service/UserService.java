package mephi.service;

import mephi.dto.request.LoginRequest;
import mephi.dto.request.RegistrationRequest;
import mephi.exception.AuthenticationException;
import mephi.exception.UserAlreadyExistsException;
import mephi.model.User;
import mephi.repository.UserRepository;
import mephi.util.PasswordEncoder;
import mephi.util.SessionManager;

import java.util.Optional;
import java.util.UUID;

public class UserService {
    private final UserRepository userRepository;
    private final SessionManager sessionManager;

    public UserService(UserRepository userRepository, SessionManager sessionManager) {
        this.userRepository = userRepository;
        this.sessionManager = sessionManager;
    }

    public User create(RegistrationRequest dto) {
        // todo: валидация данных

        if (existLogin(dto.login())) {
            throw new UserAlreadyExistsException(dto.login());
        }

        String hashPassword = PasswordEncoder.encode(dto.password());
        User newUser = new User(dto.login(), hashPassword);
        userRepository.create(newUser);

        return newUser;
    }

    public UUID login(LoginRequest dto) {
        // todo: валидация

        Optional<User> user = userRepository.findByLogin(dto.login());

        if (user.isEmpty() || !PasswordEncoder.match(dto.password(), user.get().getHasPassword())) {
            throw new AuthenticationException("Неверный логин или пароль");
        }

        return sessionManager.createSession(user.get().getId());
    }

    public void logout(UUID token) {
        sessionManager.logout(token);
    }

    private boolean existLogin(String login) {
        Optional<User> user = userRepository.findByLogin(login);

        return user.isPresent();
    }
}
