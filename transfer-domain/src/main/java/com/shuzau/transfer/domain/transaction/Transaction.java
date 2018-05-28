package com.shuzau.transfer.domain.transaction;

import com.shuzau.transfer.domain.core.Money;
import com.shuzau.transfer.domain.transfer.TransferId;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import static lombok.AccessLevel.PRIVATE;

@EqualsAndHashCode
@ToString
@Getter
@RequiredArgsConstructor(access = PRIVATE)
public class Transaction {

    @NonNull
    private final AccountId accountId;
    @NonNull
    private final TransactionId id;
    @NonNull
    private final Money amount;

    private final Transaction previousTransaction;

    private final TransferId transferId;

    public static Builder createNewAccountTransaction(@NonNull AccountId accountId, @NonNull Money initialBalance) {
        return TransactionBuilder.builder()
                                 .accountId(accountId)
                                 .amount(initialBalance);
    }

    public boolean isInitial() {
        return previousTransaction == null;
    }

    public Builder nextTransaction(@NonNull Money amount) {
        return TransactionBuilder.builder()
                                 .previousTransaction(this)
                                 .accountId(getAccountId())
                                 .amount(amount);
    }

    public Builder nextTransferTransaction(@NonNull TransferId transferId, @NonNull Money amount) {
        return TransactionBuilder.builder()
                                 .previousTransaction(this)
                                 .accountId(getAccountId())
                                 .amount(amount)
                                 .transferId(transferId);
    }

    public interface Builder {

        Transaction withId(@NonNull TransactionId id);
    }

    private static class TransactionBuilder implements Builder {

        private AccountId accountId;
        private Money amount;
        private Transaction previousTransaction;
        private TransferId transferId;

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

        private TransactionBuilder transferId(TransferId transferId) {
            this.transferId = transferId;
            return this;
        }

        @Override
        public Transaction withId(@NonNull TransactionId id) {
            return new Transaction(accountId, id, amount, previousTransaction, transferId);
        }
    }
}
