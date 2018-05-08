package com.shuzau.transfer.domain.secondary;

import java.util.Optional;

import com.shuzau.transfer.domain.entities.AccountId;
import com.shuzau.transfer.domain.entities.Transaction;

public interface TransactionRepository {

    AccountId newAccountId();

    Optional<Transaction> getLatestTransactionByAccountId(AccountId id);

    void save(Transaction transaction);

    void delete(AccountId id);
}
