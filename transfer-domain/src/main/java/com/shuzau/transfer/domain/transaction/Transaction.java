package com.shuzau.transfer.domain.transaction;

import com.shuzau.transfer.domain.core.AccountId;
import com.shuzau.transfer.domain.core.Money;
import lombok.NonNull;

public interface Transaction {

    static TransactionBuilder builder() {
        return new TransactionBuilder();
    }

    static TransactionBuilder createNewAccountTransaction(@NonNull AccountId accountId, @NonNull Money initialBalance) {
        return Transaction.builder()
                          .accountId(accountId)
                          .amount(initialBalance);
    }

    boolean isInitial();

    AccountId getAccountId();

    TransactionId getId();

    Money getAmount();

    Transaction getPreviousTransaction();

    default TransactionBuilder nextWithdrawTransaction(@NonNull Money amount) {
        return Transaction.builder()
                          .previousTransaction(this)
                          .accountId(getAccountId())
                          .amount(amount.negate());
    }

    default TransactionBuilder nextDepositTransaction(@NonNull Money amount) {
        return Transaction.builder()
                          .previousTransaction(this)
                          .accountId(getAccountId())
                          .amount(amount);
    }

    class TransactionBuilder {

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

        public Transaction withId(@NonNull TransactionId id) {
            return new BasicTransaction(accountId, id, amount, previousTransaction);
        }
    }
}
