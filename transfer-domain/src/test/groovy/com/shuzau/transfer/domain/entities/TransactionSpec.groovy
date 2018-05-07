package com.shuzau.transfer.domain.entities

import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

import static com.shuzau.transfer.domain.entities.Money.usd
import static com.shuzau.transfer.domain.entities.Transaction.createNewAccountTransaction

@Unroll
class TransactionSpec extends Specification {
    @Shared
    private defaultAccountId = AccountId.of(1L)

    def "Should create transaction for new account with #initialBalance #currency balance"() {
        given:
            Transaction transaction = createNewAccountTransaction(newAccountId, Money.builder()
                                                                                     .amount(initialBalance)
                                                                                     .currency(Currency.getInstance(currency))
                                                                                     .build())
        expect:
            with(transaction) {
                accountId == newAccountId
                amount.amount == initialBalance
                amount.currency == Currency.getInstance(currency)
                id == TransactionId.initial()
            }
        where:
            newAccountId     | initialBalance | currency
            AccountId.of(1L) | 10.0           | 'GBP'
            AccountId.of(2L) | -10.0          | 'USD'
    }

    def "Should throw NullPointerException with balance == #initialBalance account id == #accountId on createNewAccountTransaction"() {
        when:
            createNewAccountTransaction(defaultAcountId, initialBalance)
        then:
            thrown(NullPointerException)
        where:
            defaultAcountId        | initialBalance
            null             | usd(10.0)
            AccountId.of(1L) | null
    }

    def "Should create withdraw transaction with #initialBalance #currency balance"() {
        given:
            Transaction initialTransaction = createNewAccountTransaction(defaultAccountId, usd(initialBalance))
        when:
            Transaction withdrawTransaction = initialTransaction.nextWithdrawTransaction(usd(withdrawAmount))
        then:
            with(withdrawTransaction) {
                accountId == defaultAccountId
                amount == usd(initialBalance - withdrawAmount)
            }
        where:
            initialBalance | withdrawAmount | currency
            10.0           | 5.0            | 'GBP'
            -10.0          | 10.0           | 'USD'
    }
}
