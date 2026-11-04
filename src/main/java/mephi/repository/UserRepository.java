package mephi.repository;

import mephi.model.User;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class UserRepository {
    private final Map<UUID, User> users = new ConcurrentHashMap<>();
    private final Map<String, UUID> loginIndex = new ConcurrentHashMap<>();

    public void create(User user) {
        users.put(user.getId(), user);
        loginIndex.put(user.getLogin(), user.getId());
    }

    public Optional<User> findById(UUID id) {
        return Optional.ofNullable(users.get(id));
    }

    public Optional<User> findByLogin(String login) {
        UUID id = loginIndex.get(login);
        return id == null ? Optional.empty() : findById(id);
    }

    public List<User> findAll() {
        return List.copyOf(users.values());
    }
}
