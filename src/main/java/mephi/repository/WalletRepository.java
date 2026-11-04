package mephi.repository;

import mephi.model.Wallet;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class WalletRepository {
    private final Map<UUID, Wallet> wallets = new ConcurrentHashMap<>();
    private final Map<UUID, UUID> walletIndex = new ConcurrentHashMap<>();

    public void save(Wallet wallet) {
        wallets.put(wallet.getId(), wallet);
        walletIndex.put(wallet.getUserId(), wallet.getId());
    }

    public void update(Wallet wallet) {
        wallets.put(wallet.getId(), wallet);
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
}
