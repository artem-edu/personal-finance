package mephi.repository;

import com.google.gson.reflect.TypeToken;
import mephi.core.FileRepository;
import mephi.model.Wallet;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class WalletRepository extends FileRepository<UUID, Wallet> {
    private static final String FILE_DB = "db/wallet.json";
    private final Map<UUID, UUID> walletIndex = new ConcurrentHashMap<>();

    public WalletRepository() {
        this(FILE_DB);
    }

    public WalletRepository(String filedDb) {
        super(filedDb, new TypeToken<ConcurrentHashMap<UUID, Wallet>>() {
        });
        loadFromFile();
    }

    public void save(Wallet wallet) {
        data.put(wallet.getId(), wallet);
        walletIndex.put(wallet.getUserId(), wallet.getId());
        saveToFile();
    }

    public void update(Wallet wallet) {
        data.put(wallet.getId(), wallet);
        saveToFile();
    }

    public Optional<Wallet> findById(UUID id) {
        return Optional.ofNullable(data.get(id));
    }

    public Optional<Wallet> findByUserId(UUID userId) {
        UUID walletId = walletIndex.get(userId);
        return walletId == null ? Optional.empty() : findById(walletId);
    }

    public List<Wallet> findAll() {
        return List.copyOf(data.values());
    }

    @Override
    protected void afterLoad(Map<UUID, Wallet> loadedData) {
        for (Wallet wallet : loadedData.values()) {
            this.walletIndex.put(wallet.getUserId(), wallet.getId());
        }
    }
}
