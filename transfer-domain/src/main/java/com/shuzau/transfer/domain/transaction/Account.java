package com.shuzau.transfer.domain.transaction;

import com.shuzau.transfer.domain.core.Money;
import com.shuzau.transfer.domain.exception.TransferException;
import com.shuzau.transfer.domain.secondary.TransactionRepository;
import com.shuzau.transfer.domain.transfer.TransferId;
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


    public static Builder from(@NonNull Transaction transaction) {
        return new FromTransactionBuilder(transaction);
    }

    public static Builder newAccount(@NonNull Money balance) {
        return new NewAccountBuilder(balance);
    }

    public void withdraw(@NonNull Money amount) {
        assertPositive(amount);
        assertSufficientFunds(amount);
        latestTransaction = transactionRepository.save(latestTransaction.nextTransaction(amount.negate())
                                                                        .withId(transactionRepository.nextTransactionId()));
    }

    public void deposit(@NonNull Money amount) {
        assertPositive(amount);
        latestTransaction = transactionRepository.save(latestTransaction.nextTransaction(amount)
                                                                        .withId(transactionRepository.nextTransactionId()));
    }

    public void transfer(@NonNull TransferId transferId, @NonNull Money amount) {
        if (amount.isNegative()) {
            assertSufficientFunds(amount.negate());
        }
        latestTransaction = transactionRepository.save(latestTransaction.nextTransferTransaction(transferId, amount)
                                                                        .withId(transactionRepository.nextTransactionId()));
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

    private void assertPositive(Money amount) {
        if (amount.isNegative()) {
            throw new TransferException("Amount can't be negative: " + amount);
        }
    }

    private void assertSufficientFunds(@NonNull Money amount) {
        Money diff = getBalance().subtract(amount);
        if (diff.isNegative()) {
            throw new TransferException("Insufficient funds: " + diff);
        }
    }

    public interface Builder {

        Account withRepository(@NonNull TransactionRepository transactionRepository);
    }

    private static class FromTransactionBuilder implements Builder {

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

    private static class NewAccountBuilder implements Builder {

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
