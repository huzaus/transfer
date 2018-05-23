package com.shuzau.transfer.domain.transaction;

import com.shuzau.transfer.domain.core.AccountId;
import com.shuzau.transfer.domain.core.Money;
import lombok.NonNull;

import static com.shuzau.transfer.domain.transaction.TransferType.IN;
import static com.shuzau.transfer.domain.transaction.TransferType.OUT;

public interface Transfer extends Transaction {

    AccountId getSourceAccountId();

    TransferType getType();

    class TransferBuilder implements Builder {

        private AccountId accountId;
        private Money amount;
        private Transaction previousTransaction;
        private TransferType type;
        private AccountId sourceAccountId;

        private TransferBuilder() {
        }

        static TransferBuilder builder() {
            return new TransferBuilder();
        }

        private TransferBuilder accountId(AccountId accountId) {
            this.accountId = accountId;
            return this;
        }

        private TransferBuilder amount(Money amount) {
            this.amount = amount;
            return this;
        }

        private TransferBuilder previousTransaction(Transaction previousTransaction) {
            this.previousTransaction = previousTransaction;
            return this;
        }

        private TransferBuilder sourceAccountId(AccountId sourceAccountId) {
            this.sourceAccountId = sourceAccountId;
            return this;
        }

        private TransferBuilder type(TransferType type) {
            this.type = type;
            return this;
        }

        @Override
        public Transaction withId(@NonNull TransactionId id) {
            return BasicTransfer.builder()
                                .transaction(new BasicTransaction(accountId, id, amount, previousTransaction))
                                .sourceAccountId(sourceAccountId)
                                .type(type)
                                .build();
        }
    }

    default Builder nextInTransfer(@NonNull Money amount, @NonNull AccountId sourceAccountId) {
        return TransferBuilder.builder()
                              .previousTransaction(this)
                              .accountId(getAccountId())
                              .sourceAccountId(sourceAccountId)
                              .type(IN)
                              .amount(amount);
    }

    default Builder nextOutTransfer(@NonNull Money amount, @NonNull AccountId sourceAccountId) {
        return TransferBuilder.builder()
                              .previousTransaction(this)
                              .accountId(getAccountId())
                              .sourceAccountId(sourceAccountId)
                              .type(OUT)
                              .amount(amount.negate());
    }

}
