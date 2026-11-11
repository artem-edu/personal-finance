package mephi.repository;


import com.google.gson.reflect.TypeToken;
import mephi.core.FileRepository;
import mephi.model.User;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class UserRepository extends FileRepository<UUID, User> {
    private static final String FILE_DB = "db/users.json";
    private final Map<String, UUID> loginIndex = new ConcurrentHashMap<>();

    public UserRepository() {
        this(FILE_DB);
    }

    public UserRepository(String fileDb) {
        super(fileDb, new TypeToken<ConcurrentHashMap<UUID, User>>() {
        });
        this.loadFromFile();
    }

    public void create(User user) {
        data.put(user.getId(), user);
        loginIndex.put(user.getLogin(), user.getId());
        saveToFile();
    }

    public Optional<User> findById(UUID id) {
        return Optional.ofNullable(data.get(id));
    }

    public Optional<User> findByLogin(String login) {
        UUID id = loginIndex.get(login);
        return id == null ? Optional.empty() : findById(id);
    }

    public List<User> findAll() {
        return List.copyOf(data.values());
    }

    @Override
    protected void afterLoad(Map<UUID, User> loadedUsers) {
        for (User user : loadedUsers.values()) {
            this.loginIndex.put(user.getLogin(), user.getId());
        }
    }
}
