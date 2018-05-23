package com.shuzau.transfer.domain.transaction;

import com.shuzau.transfer.domain.core.AccountId;
import com.shuzau.transfer.domain.core.Money;
import lombok.NonNull;

public interface Transaction {


    static Builder createNewAccountTransaction(@NonNull AccountId accountId, @NonNull Money initialBalance) {
        return BasicTransactionBuilder.builder()
                                      .accountId(accountId)
                                      .amount(initialBalance);
    }

    boolean isInitial();

    AccountId getAccountId();

    TransactionId getId();

    Money getAmount();

    Transaction getPreviousTransaction();

    default Builder nextWithdrawTransaction(@NonNull Money amount) {
        return BasicTransactionBuilder.builder()
                                      .previousTransaction(this)
                                      .accountId(getAccountId())
                                      .amount(amount.negate());
    }

    default Builder nextDepositTransaction(@NonNull Money amount) {
        return BasicTransactionBuilder.builder()
                                      .previousTransaction(this)
                                      .accountId(getAccountId())
                                      .amount(amount);
    }

    interface Builder {

        Transaction withId(@NonNull TransactionId id);
    }

    class BasicTransactionBuilder implements Builder {

        private AccountId accountId;
        private Money amount;
        private Transaction previousTransaction;

        private BasicTransactionBuilder() {
        }

        static BasicTransactionBuilder builder() {
            return new BasicTransactionBuilder();
        }

        private BasicTransactionBuilder accountId(AccountId accountId) {
            this.accountId = accountId;
            return this;
        }

        private BasicTransactionBuilder amount(Money amount) {
            this.amount = amount;
            return this;
        }

        private BasicTransactionBuilder previousTransaction(Transaction previousTransaction) {
            this.previousTransaction = previousTransaction;
            return this;
        }

        @Override
        public Transaction withId(@NonNull TransactionId id) {
            return new BasicTransaction(accountId, id, amount, previousTransaction);
        }
    }
}