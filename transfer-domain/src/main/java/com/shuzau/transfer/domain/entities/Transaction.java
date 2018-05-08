package com.shuzau.transfer.domain.entities;

import lombok.NonNull;
import lombok.Value;


@Value
public class Transaction {

    private final AccountId accountId;
    private final TransactionId id;
    private final Money amount;
    private final Transaction previousTransaction;

    private Transaction(AccountId accountId, TransactionId id, Money amount, Transaction previousTransaction) {
        this.accountId = accountId;
        this.id = id;
        this.amount = amount;
        this.previousTransaction = previousTransaction;
    }

    public static Transaction createNewAccountTransaction(@NonNull AccountId accountId, @NonNull Money initialBalance) {
        return Transaction.builder()
                          .id(TransactionId.initial())
                          .accountId(accountId)
                          .amount(initialBalance)
                          .build();
    }

    private static TransactionBuilder builder() {
        return new TransactionBuilder();
    }

    public boolean isInitial() {
        return id.isInitial();
    }

    public Transaction nextWithdrawTransaction(@NonNull Money amount) {
        return Transaction.builder()
                          .previousTransaction(this)
                          .id(id.nextId())
                          .accountId(accountId)
                          .amount(amount.negate())
                          .build();
    }

    public Transaction nextDepositTransaction(@NonNull Money amount) {
        return Transaction.builder()
                          .previousTransaction(this)
                          .id(id.nextId())
                          .accountId(accountId)
                          .amount(amount)
                          .build();
    }

    private static class TransactionBuilder {

        private AccountId accountId;
        private TransactionId id;
        private Money amount;
        private Transaction previousTransaction;

        private TransactionBuilder() {
        }

        private TransactionBuilder accountId(AccountId accountId) {
            this.accountId = accountId;
            return this;
        }

        private TransactionBuilder id(TransactionId id) {
            this.id = id;
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

        private Transaction build() {
            return new Transaction(accountId, id, amount, previousTransaction);
        }
    }
}
