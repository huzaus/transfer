package com.shuzau.transfer.domain.core;

import java.math.BigDecimal;
import java.util.Currency;

import io.vavr.control.Validation;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

import static com.shuzau.transfer.domain.core.Validators.validator;
import static io.vavr.control.Validation.invalid;
import static io.vavr.control.Validation.valid;

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

    public boolean isNegative() {
        return amount.signum() == -1;
    }

    public Money add(Money money) {
        validator().accept(validateCurrency(money));

        return Money.builder()
                    .amount(this.amount.add(money.amount))
                    .currency(currency)
                    .build();
    }

    public Money subtract(Money money) {
        validator().accept(validateCurrency(money));

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

    public Validation<String, Money> validateCurrency(@NonNull Money money) {
        return currency != money.currency ?
            invalid("Can't operate on different currencies: " + currency + " and " + money.currency) :
            valid(money);
    }
}
