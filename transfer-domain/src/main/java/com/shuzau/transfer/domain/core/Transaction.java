package com.shuzau.transfer.domain.core;

import lombok.NonNull;


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

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Transaction)) {
            return false;
        }
        final Transaction other = (Transaction) o;
        final Object this$accountId = this.getAccountId();
        final Object other$accountId = other.getAccountId();
        if (this$accountId == null ? other$accountId != null : !this$accountId.equals(other$accountId)) {
            return false;
        }
        final Object this$id = this.getId();
        final Object other$id = other.getId();
        if (this$id == null ? other$id != null : !this$id.equals(other$id)) {
            return false;
        }
        final Object this$amount = this.getAmount();
        final Object other$amount = other.getAmount();
        if (this$amount == null ? other$amount != null : !this$amount.equals(other$amount)) {
            return false;
        }
        final Object this$previousTransaction = this.getPreviousTransaction();
        final Object other$previousTransaction = other.getPreviousTransaction();
        if (this$previousTransaction == null ? other$previousTransaction != null : !this$previousTransaction.equals(other$previousTransaction)) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $accountId = this.getAccountId();
        result = result * PRIME + ($accountId == null ? 43 : $accountId.hashCode());
        final Object $id = this.getId();
        result = result * PRIME + ($id == null ? 43 : $id.hashCode());
        final Object $amount = this.getAmount();
        result = result * PRIME + ($amount == null ? 43 : $amount.hashCode());
        final Object $previousTransaction = this.getPreviousTransaction();
        result = result * PRIME + ($previousTransaction == null ? 43 : $previousTransaction.hashCode());
        return result;
    }

    public String toString() {
        return "Transaction(accountId=" + this.getAccountId() + ", id=" + this.getId() + ", amount=" + this.getAmount() + ", previousTransaction=" + this
            .getPreviousTransaction() + ")";
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
