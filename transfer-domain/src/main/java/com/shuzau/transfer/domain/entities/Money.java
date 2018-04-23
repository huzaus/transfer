package com.shuzau.transfer.domain.entities;

import java.math.BigDecimal;
import java.util.Currency;

import com.shuzau.transfer.domain.exception.TransferException;
import lombok.Builder;
import lombok.Value;

import static java.util.Objects.requireNonNull;

@Value
@Builder
public class Money {

    private final BigDecimal amount;
    private final Currency currency;

    private Money(BigDecimal amount, Currency currency) {
        validate(amount);
        validate(currency);
        this.amount = amount;
        this.currency = currency;
    }

    public static Money usd(BigDecimal amount) {
        return new Money(amount, Currency.getInstance("USD"));
    }

    public static Money gbp(BigDecimal amount) {
        return new Money(amount, Currency.getInstance("GBP"));
    }

    public static Money pln(BigDecimal amount) {
        return new Money(amount, Currency.getInstance("PLN"));
    }

    public boolean isPositive() {
        return amount.signum() > -1;
    }

    public Money add(Money money) {
        validate(money);
        return Money.builder()
                    .amount(this.amount.add(money.amount))
                    .currency(currency)
                    .build();
    }

    public Money subtract(Money money) {
        validate(money);
        return Money.builder()
                    .amount(this.amount.subtract(money.amount))
                    .currency(currency)
                    .build();
    }

    private void validate(Money money) {
        requireNonNull(money);
        if (currency != money.currency) {
            throw new TransferException("Could't operate on different currencies: " + currency + " and " + money.currency);
        }
    }

    private void validate(BigDecimal amount) {
        requireNonNull(amount);
    }

    private void validate(Currency currency) {
        requireNonNull(currency);
    }
}
