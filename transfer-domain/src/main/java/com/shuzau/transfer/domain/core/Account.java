package com.shuzau.transfer.domain.core;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import com.shuzau.transfer.domain.exception.TransferException;
import com.shuzau.transfer.domain.secondary.TransactionRepository;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import static com.shuzau.transfer.domain.core.Transaction.createNewAccountTransaction;
import static lombok.AccessLevel.PRIVATE;

@RequiredArgsConstructor(access = PRIVATE)
@Getter
@EqualsAndHashCode
@ToString
public class Account {

    private final AccountId id;
    private final TransactionRepository transactionRepository;
    private AtomicReference<Transaction> latestTransaction;


    static FromTransactionBuilder from(@NonNull Transaction transaction) {
        return new FromTransactionBuilder(transaction);
    }

    static NewAccountBuilder newAccount(@NonNull Money balance) {
        return new NewAccountBuilder(balance);
    }

    public void withdraw(@NonNull Money amount) {
        validate(amount);
        Optional.of(amount)
                .map(getBalance()::subtract)
                .filter(Money::isPositive)
                .orElseThrow(() -> new TransferException("Insufficient funds"));
        latestTransaction.getAndUpdate(transaction -> transactionRepository.save(transaction.nextWithdrawTransaction(amount)));
    }

    public void deposit(@NonNull Money amount) {
        validate(amount);
        latestTransaction.getAndUpdate(transaction -> transactionRepository.save(transaction.nextDepositTransaction(amount)));
    }

    private void validate(Money amount) {
        Optional.of(amount)
                .filter(Money::isPositive)
                .orElseThrow(() -> new TransferException("Amount can't be negative: " + amount));
    }

    public Money getBalance() {
        Transaction currentTransaction = latestTransaction.get();
        Money balance = currentTransaction.getAmount();
        while (!currentTransaction.isInitial()) {
            currentTransaction = currentTransaction.getPreviousTransaction();
            balance = balance.add(currentTransaction.getAmount());
        }
        return balance;
    }

    static class FromTransactionBuilder {

        private final Transaction latestTransaction;

        private FromTransactionBuilder(Transaction transaction) {
            latestTransaction = transaction;
        }

        Account withRepository(@NonNull TransactionRepository transactionRepository) {
            Account account = new Account(latestTransaction.getAccountId(), transactionRepository);
            account.latestTransaction = new AtomicReference<>(latestTransaction);
            return account;
        }
    }

    static class NewAccountBuilder {

        private final Money balance;

        private NewAccountBuilder(Money balance) {
            this.balance = balance;
        }

        Account withRepository(@NonNull TransactionRepository transactionRepository) {
            AccountId accountId = transactionRepository.newAccountId();
            Account account = new Account(accountId, transactionRepository);
            account.latestTransaction = new AtomicReference<>(transactionRepository.save(createNewAccountTransaction(accountId, balance)));
            return account;
        }
    }
}
