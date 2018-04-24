package com.shuzau.transfer.domain.entities;

import java.util.Optional;

import com.shuzau.transfer.domain.exception.TransferException;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

@Builder
@Getter
@EqualsAndHashCode
@ToString
public class Account {

    @NonNull
    private final AccountId id;
    @NonNull
    private Money balance;

    public Account withdraw(Money amount) {
        validate(amount);
        balance = Optional.of(amount)
                          .map(balance::subtract)
                          .filter(Money::isPositive)
                          .orElseThrow(() -> new TransferException("Insufficient funds"));
        return this;
    }

    public Account deposit(Money amount) {
        validate(amount);
        balance = balance.add(amount);
        return this;
    }

    private void validate(Money amount) {
        Optional.of(amount)
                .filter(Money::isPositive)
                .orElseThrow(() -> new TransferException("Amount can't be negative: " + amount));
    }

}
