package mephi.repository;

import com.google.gson.reflect.TypeToken;
import mephi.core.FileRepository;
import mephi.model.Category;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class CategoryRepository extends FileRepository<UUID, Category> {
    private static final String FILE_DB = "db/categories.json";

    public CategoryRepository() {
        this(FILE_DB);
    }

    public CategoryRepository(String fileDb) {
        super(fileDb, new TypeToken<ConcurrentHashMap<UUID, Category>>() {
        });
        loadFromFile();
    }

    public void save(Category category) {
        data.put(category.getId(), category);
        saveToFile();
    }

    public Optional<Category> findById(UUID id) {
        return Optional.ofNullable(data.get(id));
    }

    public Optional<Category> findByWalletAndName(UUID walletId, String name) {
        return findByWallet(walletId).stream().filter(c -> c.getName().equalsIgnoreCase(name.trim())).findFirst();
    }

    public List<Category> findByWallet(UUID walletId) {
        if (walletId == null) return List.of();

        return data.values().stream().filter(c -> c.getWalletId().equals(walletId)).collect(Collectors.toList());
    }

    public List<Category> findAll() {
        return List.copyOf(data.values());
    }
}
