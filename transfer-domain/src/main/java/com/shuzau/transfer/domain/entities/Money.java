package com.shuzau.transfer.domain.entities;

import java.math.BigDecimal;
import java.util.Currency;

import com.shuzau.transfer.domain.exception.TransferException;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Value
@Builder
public class Money {

    private final BigDecimal amount;
    private final Currency currency;

    private Money(@NonNull BigDecimal amount, @NonNull Currency currency) {
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

    public Money negate() {
        return Money.builder()
                    .amount(this.amount.negate())
                    .currency(currency)
                    .build();
    }

    private void validate(@NonNull Money money) {
        if (currency != money.currency) {
            throw new TransferException("Can't operate on different currencies: " + currency + " and " + money.currency);
        }
    }
}
