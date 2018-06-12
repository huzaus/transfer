package com.shuzau.transfer.domain.core

import com.shuzau.transfer.domain.exception.TransferException
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

import static com.shuzau.transfer.domain.core.Money.gbp
import static com.shuzau.transfer.domain.core.Money.pln
import static com.shuzau.transfer.domain.core.Money.usd

@Unroll
class MoneySpec extends Specification {

    @Shared
    private Currency USD = Currency.getInstance("USD")

    def "Should throw #exception when currency = #currency and amount = #amount"() {
        when:
            Money.builder()
                 .amount(amount)
                 .currency(currency)
                 .build()
        then:
            thrown(exception)
        where:
            currency | amount || exception
            null     | 1.0    || NullPointerException
            USD      | null   || NullPointerException
    }

    def "Should throw #excpetion when adding #moneyToAdd to #money"() {
        when:
            money.add(moneyToAdd)
        then:
            thrown(excpetion)
        where:
            money     | moneyToAdd || excpetion
            usd(10.0) | null       || NullPointerException
            usd(10.0) | gbp(10.0)  || TransferException
    }

    def "Should throw #excpetion when subtracting #moneyToSubtract from #money"() {
        when:
            money.add(moneyToSubtract)
        then:
            thrown(excpetion)
        where:
            money     | moneyToSubtract || excpetion
            usd(10.0) | null            || NullPointerException
            usd(10.0) | gbp(10.0)       || TransferException
    }

    def "#money + #moneyToAdd = #sum"() {
        when:
            Money result = money.add(moneyToAdd)
        then:
            result == sum
        where:
            money     | moneyToAdd || sum
            usd(10.0) | usd(0.0)   || usd(10.0)
            gbp(10.0) | gbp(1.0)   || gbp(11.0)
            pln(10.0) | pln(-1.0)  || pln(9.0)
    }

    def "#money - #moneyToSubtract = #difference"() {
        when:
            Money result = money.subtract(moneyToSubtract)
        then:
            result == difference
        where:
            money     | moneyToSubtract || difference
            usd(10.0) | usd(0.0)        || usd(10.0)
            gbp(10.0) | gbp(1.0)        || gbp(9.0)
            pln(10.0) | pln(-1.0)       || pln(11.0)
    }

    def "Should return #isPositive for #money on isPositive"() {
        expect:
            money.isPositive() == isPositive
        where:
            money     || isPositive
            usd(10.0) || true
            gbp(0.0)  || true
            gbp(-0.0) || true
            pln(-1.0) || false
    }

    def "Should return #isNegative for #money on isNegative"() {
        expect:
            money.isNegative() == isNegative
        where:
            money     || isNegative
            usd(10.0) || false
            gbp(0.0)  || false
            gbp(-0.0) || false
            pln(-1.0) || true
    }

    def "Should negate #amount on negate"() {
        given:
            Money money = usd(amount)
        when:
            Money negatedAmount = money.negate()
        then:
            negatedAmount == usd(expectedAmout)
        where:
            amount || expectedAmout
            10.0   || -10.0
            -10.0  || 10.0
            0.0    || 0.0
    }
}
