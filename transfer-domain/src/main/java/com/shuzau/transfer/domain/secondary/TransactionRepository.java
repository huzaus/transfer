package com.shuzau.transfer.domain.secondary;

import java.util.Optional;

import com.shuzau.transfer.domain.transaction.AccountId;
import com.shuzau.transfer.domain.transaction.Transaction;
import com.shuzau.transfer.domain.transaction.TransactionId;

public interface TransactionRepository {

    AccountId newAccountId();

    TransactionId nextTransactionId();

    Optional<Transaction> getLatestTransactionByAccountId(AccountId id);

    Transaction save(Transaction transaction);

    void delete(AccountId id);
}
