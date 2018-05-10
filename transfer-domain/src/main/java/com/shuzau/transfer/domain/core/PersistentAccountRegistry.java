package com.shuzau.transfer.domain.core;

import java.util.Optional;

import com.shuzau.transfer.domain.primary.AccountRegistry;
import com.shuzau.transfer.domain.secondary.TransactionRepository;
import lombok.NonNull;
import lombok.Value;

@Value
class PersistentAccountRegistry implements AccountRegistry {

    private final TransactionRepository transactionRepository;

    @Override
    public Optional<Account> findAccountById(@NonNull AccountId id) {
        return transactionRepository.getLatestTransactionByAccountId(id)
                                    .map(transaction -> Account.from(transaction)
                                                               .withRepository(transactionRepository));
    }

    @Override
    public Account createAccountWithBalance(@NonNull Money balance) {
        return Account.newAccount(balance)
                      .withRepository(transactionRepository);
    }

    @Override
    public void deleteAccount(AccountId id) {
        transactionRepository.delete(id);
    }
}
