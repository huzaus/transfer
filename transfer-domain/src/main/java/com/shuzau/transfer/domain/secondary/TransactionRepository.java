package com.shuzau.transfer.domain.secondary;

import java.util.Optional;

import com.shuzau.transfer.domain.core.AccountId;
import com.shuzau.transfer.domain.core.Transaction;
import com.shuzau.transfer.domain.core.TransactionId;

public interface TransactionRepository {

    AccountId newAccountId();

    TransactionId nextTransactionId();

    Optional<Transaction> getLatestTransactionByAccountId(AccountId id);

    Transaction save(Transaction transaction);

    void delete(AccountId id);
}
