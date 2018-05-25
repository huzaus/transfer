package com.shuzau.transfer.domain.transaction;

import com.shuzau.transfer.domain.core.AccountId;
import com.shuzau.transfer.domain.core.Money;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

@EqualsAndHashCode
@ToString
@Getter
public class Transaction {

    @NonNull
    private final AccountId accountId;
    @NonNull
    private final TransactionId id;
    @NonNull
    private final Money amount;

    private final Transaction previousTransaction;

    private Transaction(AccountId accountId, TransactionId id, Money amount, Transaction previousTransaction) {
        this.accountId = accountId;
        this.id = id;
        this.amount = amount;
        this.previousTransaction = previousTransaction;
    }

    public static Builder createNewAccountTransaction(@NonNull AccountId accountId, @NonNull Money initialBalance) {
        return TransactionBuilder.builder()
                                 .accountId(accountId)
                                 .amount(initialBalance);
    }

    public boolean isInitial() {
        return previousTransaction == null;
    }

    public Builder nextWithdrawTransaction(@NonNull Money amount) {
        return TransactionBuilder.builder()
                                 .previousTransaction(this)
                                 .accountId(getAccountId())
                                 .amount(amount.negate());
    }

    public Builder nextDepositTransaction(@NonNull Money amount) {
        return TransactionBuilder.builder()
                                 .previousTransaction(this)
                                 .accountId(getAccountId())
                                 .amount(amount);
    }

    public interface Builder {

        Transaction withId(@NonNull TransactionId id);
    }

    private static class TransactionBuilder implements Builder {

        private AccountId accountId;
        private Money amount;
        private Transaction previousTransaction;

        private TransactionBuilder() {
        }

        private static TransactionBuilder builder() {
            return new TransactionBuilder();
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

        @Override
        public Transaction withId(@NonNull TransactionId id) {
            return new Transaction(accountId, id, amount, previousTransaction);
        }
    }
}
