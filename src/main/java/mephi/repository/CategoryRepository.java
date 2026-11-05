package mephi.repository;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import mephi.model.Category;
import mephi.model.User;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class CategoryRepository {
    private static final String FILE_DB = "db/categories.json";
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private final Map<UUID, Category> categories = new ConcurrentHashMap<>();

    public CategoryRepository() {
        loadFromFile();
    }

    public void save(Category category) {
        categories.put(category.getId(), category);
        saveToFile();
    }

    public Optional<Category> findById(UUID id) {
        return Optional.ofNullable(categories.get(id));
    }

    public List<Category> findByWallet(UUID walletId) {
        if (walletId == null) return List.of();

        return categories.values()
                .stream()
                .filter(c -> c.getWalletId().equals(walletId))
                .collect(Collectors.toList());
    }

    public List<Category> findAll() {
        return List.copyOf(categories.values());
    }

    private synchronized void saveToFile() {
        try (FileWriter writer = new FileWriter(FILE_DB)) {
            gson.toJson(categories, writer);
        } catch (IOException e) {
            System.err.println("Ошибка при сохранении категорий в файл: " + e.getMessage());
        }
    }

    private void loadFromFile() {
        File file = new File(FILE_DB);

        if (!file.exists()) {
            return;
        }

        try (FileReader reader = new FileReader(file)) {
            Type mapType = new TypeToken<ConcurrentHashMap<UUID, Category>>() {
            }.getType();

            Map<UUID, Category> loadedCategories = gson.fromJson(reader, mapType);

            if (loadedCategories != null) {
                this.categories.putAll(loadedCategories);
            }
        } catch (IOException e) {
            System.err.println("Ошибка при чтении пользователей из файла: " + e.getMessage());
        }
    }
}
