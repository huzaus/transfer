package com.shuzau.transfer.domain.configuration;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import com.shuzau.transfer.domain.entities.AccountId;
import com.shuzau.transfer.domain.entities.Transaction;
import com.shuzau.transfer.domain.exception.TransferException;
import com.shuzau.transfer.domain.secondary.TransactionRepository;
import lombok.NonNull;

import static java.util.Objects.requireNonNull;

class InMemoryTransactionRepository implements TransactionRepository {

    private final Map<AccountId, Transaction> storage = new ConcurrentHashMap<>();

    private final AtomicLong sequence = new AtomicLong(0L);

    @Override
    public AccountId newAccountId() {
        return AccountId.of(sequence.incrementAndGet());
    }

    @Override
    public Optional<Transaction> getLatestTransactionByAccountId(@NonNull AccountId id) {
        return Optional.ofNullable(storage.get(id));
    }


    @Override
    public void save(@NonNull Transaction newTransaction) {
        Transaction savedTransaction = Optional.of(newTransaction)
                                               .filter(Transaction::isInitial)
                                               .map(transaction -> Optional.ofNullable(storage.putIfAbsent(transaction.getAccountId(), transaction))
                                                                           .orElse(transaction))
                                               .orElseGet(() -> storage.compute(newTransaction.getAccountId(),
                                                   (accountId, oldTransaction) -> compareAndGet(newTransaction, oldTransaction)));

        if (!Objects.equals(savedTransaction, newTransaction)) {
            throw new TransferException("Failed to save transaction " + newTransaction);
        }
    }

    @Override
    public void delete(@NonNull AccountId id) {
        storage.remove(id);
    }

    private Transaction compareAndGet(Transaction newTransaction, Transaction oldTransaction) {
        requireNonNull(newTransaction.getPreviousTransaction());
        return Optional.of(newTransaction)
                       .filter(transaction -> newTransaction.getPreviousTransaction().equals(oldTransaction))
                       .orElse(oldTransaction);
    }
}
