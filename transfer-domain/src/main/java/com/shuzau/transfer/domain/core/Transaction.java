package com.shuzau.transfer.domain.core;

import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class Transaction {

    private final AccountId accountId;
    private final TransactionId id;
    private final Money amount;
    private final Transaction previousTransaction;

    protected Transaction(AccountId accountId, TransactionId id, Money amount, Transaction previousTransaction) {
        this.accountId = accountId;
        this.id = id;
        this.amount = amount;
        this.previousTransaction = previousTransaction;
    }

    public static TransactionBuilder createNewAccountTransaction(@NonNull AccountId accountId, @NonNull Money initialBalance) {
        return Transaction.builder()
                          .accountId(accountId)
                          .amount(initialBalance);
    }

    private static TransactionBuilder builder() {
        return new TransactionBuilder();
    }

    public boolean isInitial() {
        return previousTransaction == null;
    }

    public TransactionBuilder nextWithdrawTransaction(@NonNull Money amount) {
        return Transaction.builder()
                          .previousTransaction(this)
                          .accountId(accountId)
                          .amount(amount.negate());
    }

    public TransactionBuilder nextDepositTransaction(@NonNull Money amount) {
        return Transaction.builder()
                          .previousTransaction(this)
                          .accountId(accountId)
                          .amount(amount);
    }

    public AccountId getAccountId() {
        return this.accountId;
    }

    public TransactionId getId() {
        return this.id;
    }

    public Money getAmount() {
        return this.amount;
    }

    public Transaction getPreviousTransaction() {
        return this.previousTransaction;
    }

    static class TransactionBuilder {

        private AccountId accountId;
        private Money amount;
        private Transaction previousTransaction;

        private TransactionBuilder() {
        }

        private TransactionBuilder accountId(AccountId accountId) {
            this.accountId = accountId;
            return this;
        }

        private TransactionBuilder amount(Money amount) {
            this.amount = amount;
            return this;
        }

        private TransactionBuilder previousTransaction(Transaction previousTransaction) {
            this.previousTransaction = previousTransaction;
            return this;
        }

        Transaction withId(@NonNull TransactionId id) {
            return new Transaction(accountId, id, amount, previousTransaction);
        }
    }
}
