package com.shuzau.transfer.domain.core;

import java.util.Optional;

import com.shuzau.transfer.domain.exception.TransferException;
import com.shuzau.transfer.domain.secondary.TransactionRepository;
import com.shuzau.transfer.domain.transaction.Transaction;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import static lombok.AccessLevel.PRIVATE;

@RequiredArgsConstructor(access = PRIVATE)
@Getter
@EqualsAndHashCode
@ToString
public class Account {

    private final AccountId id;
    private final TransactionRepository transactionRepository;
    private Transaction latestTransaction;


    static Builder from(@NonNull Transaction transaction) {
        return new FromTransactionBuilder(transaction);
    }

    static Builder newAccount(@NonNull Money balance) {
        return new NewAccountBuilder(balance);
    }

    public void withdraw(@NonNull Money amount) {
        validate(amount);
        Optional.of(amount)
                .map(getBalance()::subtract)
                .filter(Money::isPositive)
                .orElseThrow(() -> new TransferException("Insufficient funds"));
        latestTransaction = transactionRepository.save(latestTransaction.nextWithdrawTransaction(amount)
                                                                        .withId(transactionRepository.nextTransactionId()));
    }

    public void deposit(@NonNull Money amount) {
        validate(amount);
        latestTransaction = transactionRepository.save(latestTransaction.nextDepositTransaction(amount)
                                                                        .withId(transactionRepository.nextTransactionId()));
    }

    private void validate(Money amount) {
        Optional.of(amount)
                .filter(Money::isPositive)
                .orElseThrow(() -> new TransferException("Amount can't be negative: " + amount));
    }

    public Money getBalance() {
        Transaction currentTransaction = latestTransaction;
        Money balance = currentTransaction.getAmount();
        while (!currentTransaction.isInitial()) {
            currentTransaction = currentTransaction.getPreviousTransaction();
            balance = balance.add(currentTransaction.getAmount());
        }
        return balance;
    }


    interface Builder {

        Account withRepository(@NonNull TransactionRepository transactionRepository);
    }

    static class FromTransactionBuilder implements Builder {

        private final Transaction latestTransaction;

        private FromTransactionBuilder(Transaction transaction) {
            latestTransaction = transaction;
        }

        @Override
        public Account withRepository(@NonNull TransactionRepository transactionRepository) {
            Account account = new Account(latestTransaction.getAccountId(), transactionRepository);
            account.latestTransaction = latestTransaction;
            return account;
        }
    }

    static class NewAccountBuilder implements Builder {

        private final Money balance;

        private NewAccountBuilder(Money balance) {
            this.balance = balance;
        }

        @Override
        public Account withRepository(@NonNull TransactionRepository transactionRepository) {
            AccountId accountId = transactionRepository.newAccountId();
            Account account = new Account(accountId, transactionRepository);
            account.latestTransaction = transactionRepository.save(Transaction.createNewAccountTransaction(accountId, balance)
                                                                              .withId(transactionRepository.nextTransactionId()));
            return account;
        }
    }
}
