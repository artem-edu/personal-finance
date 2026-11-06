package mephi.repository;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import mephi.model.Category;
import mephi.model.Transaction;

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
import java.util.stream.Collectors;

public class TransactionRepository {
    private static final String FILE_DB = "db/transactions.json";
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private final Map<UUID, Transaction> transactions = new ConcurrentHashMap<>();

    public TransactionRepository() {
        loadFromFile();
    }

    public void save(Transaction transaction) {
        transactions.put(transaction.getId(), transaction);
        saveToFile();
    }

    public Optional<Transaction> findById(UUID id) {
        return Optional.ofNullable(transactions.get(id));
    }

    public List<Transaction> findByWalletId(UUID walletId) {
        if (walletId == null) return List.of();

        return transactions.values()
                .stream()
                .filter(t -> t.getWalletId().equals(walletId))
                .collect(Collectors.toList());
    }

    public List<Transaction> findAll() {
        return List.copyOf(transactions.values());
    }

    private synchronized void saveToFile() {
        try (FileWriter writer = new FileWriter(FILE_DB)) {
            gson.toJson(transactions, writer);
        } catch (IOException e) {
            System.err.println("Ошибка при сохранении транзакций в файл: " + e.getMessage());
        }
    }

    private void loadFromFile() {
        File file = new File(FILE_DB);

        if (!file.exists()) {
            return;
        }

        try (FileReader reader = new FileReader(file)) {
            Type mapType = new TypeToken<ConcurrentHashMap<UUID, Transaction>>() {
            }.getType();

            Map<UUID, Transaction> loadedTransactions = gson.fromJson(reader, mapType);

            if (loadedTransactions != null) {
                this.transactions.putAll(loadedTransactions);
            }
        } catch (IOException e) {
            System.err.println("Ошибка при чтении транзакций из файла: " + e.getMessage());
        }
    }
}
