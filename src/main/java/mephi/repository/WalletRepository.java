package mephi.repository;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import mephi.model.Wallet;

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

public class WalletRepository {
    private static final String FILE_DB = "db/wallet.json";
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private final Map<UUID, Wallet> wallets = new ConcurrentHashMap<>();
    private final Map<UUID, UUID> walletIndex = new ConcurrentHashMap<>();

    public WalletRepository() {
        loadFromFile();
    }

    public void save(Wallet wallet) {
        wallets.put(wallet.getId(), wallet);
        walletIndex.put(wallet.getUserId(), wallet.getId());
        saveToFile();
    }

    public void update(Wallet wallet) {
        wallets.put(wallet.getId(), wallet);
        saveToFile();
    }

    public Optional<Wallet> findById(UUID id) {
        return Optional.ofNullable(wallets.get(id));
    }

    public Optional<Wallet> findByUserId(UUID userId) {
        UUID walletId = walletIndex.get(userId);
        return walletId == null ? Optional.empty() : findById(walletId);
    }

    public List<Wallet> findAll() {
        return List.copyOf(wallets.values());
    }

    private synchronized void saveToFile() {
        try (FileWriter writer = new FileWriter(FILE_DB)) {
            gson.toJson(wallets, writer);
        } catch (IOException e) {
            System.err.println("Ошибка при сохранении кошельков в файл: " + e.getMessage());
        }
    }

    private void loadFromFile() {
        File file = new File(FILE_DB);

        if (!file.exists()) {
            return;
        }

        try (FileReader reader = new FileReader(file)) {
            Type mapType = new TypeToken<ConcurrentHashMap<UUID, Wallet>>() {
            }.getType();

            Map<UUID, Wallet> loadedWallets = gson.fromJson(reader, mapType);

            if (loadedWallets != null) {
                this.wallets.putAll(loadedWallets);

                for (Wallet wallet : loadedWallets.values()) {
                    this.walletIndex.put(wallet.getUserId(), wallet.getId());
                }
            }
        } catch (IOException e) {
            System.err.println("Ошибка при чтении кошелька из файла: " + e.getMessage());
        }
    }
}
