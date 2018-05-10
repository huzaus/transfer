package com.shuzau.transfer.domain.secondary;

import java.util.Optional;

import com.shuzau.transfer.domain.core.AccountId;
import com.shuzau.transfer.domain.core.Transaction;

public interface TransactionRepository {

    AccountId newAccountId();

    Optional<Transaction> getLatestTransactionByAccountId(AccountId id);

    Transaction save(Transaction transaction);

    void delete(AccountId id);
}
