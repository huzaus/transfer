package com.shuzau.transfer.domain.transaction;

import com.shuzau.transfer.domain.core.AccountId;
import com.shuzau.transfer.domain.core.Money;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class BasicTransaction implements Transaction {

    private final AccountId accountId;
    private final TransactionId id;
    private final Money amount;
    private final Transaction previousTransaction;

    BasicTransaction(AccountId accountId, TransactionId id, Money amount, Transaction previousTransaction) {
        this.accountId = accountId;
        this.id = id;
        this.amount = amount;
        this.previousTransaction = previousTransaction;
    }

    @Override
    public boolean isInitial() {
        return previousTransaction == null;
    }

    @Override
    public AccountId getAccountId() {
        return this.accountId;
    }

    @Override
    public TransactionId getId() {
        return this.id;
    }

    @Override
    public Money getAmount() {
        return this.amount;
    }

    @Override
    public Transaction getPreviousTransaction() {
        return this.previousTransaction;
    }


}
