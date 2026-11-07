package mephi.repository;

import com.google.gson.reflect.TypeToken;
import mephi.core.FileRepository;
import mephi.model.Transaction;
import mephi.model.enums.TransactionType;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class TransactionRepository extends FileRepository<UUID, Transaction> {
    private static final String FILE_DB = "db/transactions.json";

    public TransactionRepository() {
        super(FILE_DB, new TypeToken<ConcurrentHashMap<UUID, Transaction>>() {
        });
        loadFromFile();
    }

    public void save(Transaction transaction) {
        data.put(transaction.getId(), transaction);
        saveToFile();
    }

    public Optional<Transaction> findById(UUID id) {
        return Optional.ofNullable(data.get(id));
    }

    public List<Transaction> findByWalletId(UUID walletId) {
        return data.values()
                .stream()
                .filter(t -> t.getWalletId().equals(walletId))
                .collect(Collectors.toList());
    }

    public List<Transaction> findByCategoryId(UUID categoryId) {
        return data.values()
                .stream()
                .filter(t -> t.getCategoryId().equals(categoryId))
                .collect(Collectors.toList());
    }

    public BigDecimal findAmountByWalletAndType(UUID walletId, TransactionType type) {
        return findByWalletId(walletId)
                .stream()
                .filter(t -> t.getType() == type)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal findAmountByCategoryAndType(UUID categoryId, TransactionType type) {
        return findByCategoryId(categoryId)
                .stream()
                .filter(t -> t.getType() == type)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public List<Transaction> findAll() {
        return List.copyOf(data.values());
    }
}
