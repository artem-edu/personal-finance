package mephi.service;

import mephi.dto.request.CreateTransaction;
import mephi.exception.PersonalFinanceException;
import mephi.model.Transaction;
import mephi.model.Wallet;
import mephi.model.enums.TransactionType;
import mephi.repository.TransactionRepository;
import mephi.repository.WalletRepository;

import java.util.List;
import java.util.UUID;

public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final WalletRepository walletRepository;

    public TransactionService(TransactionRepository transactionRepository, WalletRepository walletRepository) {
        this.transactionRepository = transactionRepository;
        this.walletRepository = walletRepository;
    }

    public Transaction create(CreateTransaction dto) {
        Wallet wallet = walletRepository.findById(dto.walletId())
                .orElseThrow(() -> new PersonalFinanceException("Кошелек не найден"));

        if (dto.type() == TransactionType.DEPOSIT) {
            wallet.deposit(dto.amount());
        }

        if (dto.type() == TransactionType.WITHDRAWAL) {
            wallet.withdraw(dto.amount());
        }

        Transaction transaction = new Transaction(dto.categoryId(), wallet.getId(), dto.type(), dto.amount());
        transactionRepository.save(transaction);
        walletRepository.update(wallet);

        return transaction;
    }

    public List<Transaction> getTransactionsByWalletId(UUID walletId) {
        return transactionRepository.findByWalletId(walletId);
    }
}
