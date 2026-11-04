package mephi.service;

import mephi.exception.PersonalFinanceException;
import mephi.model.Wallet;
import mephi.repository.WalletRepository;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

public class WalletService {
    private final WalletRepository walletRepository;

    public WalletService(WalletRepository walletRepository) {
        this.walletRepository = walletRepository;
    }

    public Wallet create(UUID userId) {
        Wallet wallet = new Wallet(userId);
        walletRepository.save(wallet);

        return wallet;
    }

    public BigDecimal getBalance(UUID userId) {
        Optional<Wallet> wallet = walletRepository.findByUserId(userId);
        if (wallet.isEmpty()) {
            throw new PersonalFinanceException("Кошелек не найден");
        }

        return wallet.get().getBalance();
    }

}
