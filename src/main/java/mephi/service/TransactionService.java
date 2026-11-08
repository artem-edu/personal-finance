package mephi.service;

import mephi.dto.request.CreateTransaction;
import mephi.dto.response.TransactionResult;
import mephi.exception.PersonalFinanceException;
import mephi.model.Category;
import mephi.model.StatByCategory;
import mephi.model.Transaction;
import mephi.model.Wallet;
import mephi.model.enums.TransactionType;
import mephi.repository.CategoryRepository;
import mephi.repository.TransactionRepository;
import mephi.repository.WalletRepository;

public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final WalletRepository walletRepository;
    private final StatService statService;
    private final CategoryRepository categoryRepository;

    public TransactionService(
            TransactionRepository transactionRepository,
            WalletRepository walletRepository,
            StatService statService,
            CategoryRepository categoryRepository
    ) {
        this.transactionRepository = transactionRepository;
        this.walletRepository = walletRepository;
        this.statService = statService;
        this.categoryRepository = categoryRepository;
    }

    public TransactionResult create(CreateTransaction dto) {
        Wallet wallet = walletRepository.findById(dto.walletId())
                .orElseThrow(() -> new PersonalFinanceException("Кошелек не найден"));
        Category category = categoryRepository.findById(dto.categoryId())
                .orElseThrow(() -> new PersonalFinanceException("Категория не найдена"));

        if (dto.type() == TransactionType.WITHDRAWAL && category.getIsDepositOnly()) {
            throw new PersonalFinanceException(
                    String.format("Нельзя списывать из '%s'.\nДоступна только для пополнения", category.getName())
            );
        }

        if (dto.type() == TransactionType.DEPOSIT) {
            wallet.deposit(dto.amount());
        }

        if (dto.type() == TransactionType.WITHDRAWAL) {
            wallet.withdraw(dto.amount());
        }

        Transaction transaction = new Transaction(dto.categoryId(), wallet.getId(), dto.type(), dto.amount());
        transactionRepository.save(transaction);
        walletRepository.update(wallet);
        StatByCategory statByCategory = statService.getStatByCategory(dto.categoryId());
        boolean isLimitExceeded = statByCategory.remainingBudget().signum() < 0;

        return new TransactionResult(transaction, isLimitExceeded);
    }
}
