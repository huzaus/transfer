package com.shuzau.transfer.domain.transaction;

import com.shuzau.transfer.domain.core.AccountId;
import com.shuzau.transfer.domain.core.Money;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;

@EqualsAndHashCode
@ToString
@Builder
class OutgoingTransfer implements Transaction {

    @NonNull
    BasicTransaction transaction;

    @NonNull
    AccountId targetAccountId;

    @Override
    public boolean isInitial() {
        return false;
    }

    @Override
    public AccountId getAccountId() {
        return transaction.getAccountId();
    }

    @Override
    public TransactionId getId() {
        return transaction.getId();
    }

    @Override
    public Money getAmount() {
        return transaction.getAmount();
    }

    @Override
    public Transaction getPreviousTransaction() {
        return transaction.getPreviousTransaction();
    }

    protected AccountId getTargetAccountId() {
        return targetAccountId;
    }
}