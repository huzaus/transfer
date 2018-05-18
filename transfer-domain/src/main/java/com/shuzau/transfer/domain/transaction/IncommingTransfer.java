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
class IncommingTransfer implements Transaction {

    @NonNull
    BasicTransaction basicTransaction;

    @NonNull
    AccountId sourceAccountId;

    @Override
    public boolean isInitial() {
        return false;
    }

    @Override
    public AccountId getAccountId() {
        return basicTransaction.getAccountId();
    }

    @Override
    public TransactionId getId() {
        return basicTransaction.getId();
    }

    @Override
    public Money getAmount() {
        return basicTransaction.getAmount();
    }

    @Override
    public Transaction getPreviousTransaction() {
        return basicTransaction.getPreviousTransaction();
    }

    protected AccountId getSourceAccountId() {
        return sourceAccountId;
    }
}