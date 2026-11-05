package mephi.repository;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import mephi.model.User;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class UserRepository {
    private static final String FILE_DB = "db/users.json";
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private final Map<UUID, User> users = new ConcurrentHashMap<>();
    private final Map<String, UUID> loginIndex = new ConcurrentHashMap<>();

    public UserRepository() {
        loadFromFile();
    }

    public void create(User user) {
        users.put(user.getId(), user);
        loginIndex.put(user.getLogin(), user.getId());
        saveToFile();
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

    private synchronized void saveToFile() {
        try (FileWriter writer = new FileWriter(FILE_DB)) {
            gson.toJson(users, writer);
        } catch (IOException e) {
            System.err.println("Ошибка при сохранении пользователей в файл: " + e.getMessage());
        }
    }

    private void loadFromFile() {
        File file = new File(FILE_DB);

        if (!file.exists()) {
            return;
        }

        try (FileReader reader = new FileReader(file)) {
            Type mapType = new TypeToken<ConcurrentHashMap<UUID, User>>() {
            }.getType();

            Map<UUID, User> loadedUsers = gson.fromJson(reader, mapType);

            if (loadedUsers != null) {
                this.users.putAll(loadedUsers);

                for (User user : loadedUsers.values()) {
                    this.loginIndex.put(user.getLogin(), user.getId());
                }
            }
        } catch (IOException e) {
            System.err.println("Ошибка при чтении пользователей из файла: " + e.getMessage());
        }
    }
}
