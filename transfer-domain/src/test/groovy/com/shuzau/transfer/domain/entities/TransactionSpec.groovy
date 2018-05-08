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
            defaultAcountId  | initialBalance
            null             | usd(10.0)
            AccountId.of(1L) | null
    }

    def "Should create withdraw transaction with #withdrawAmount #currency amount"() {
        given:
            Transaction initialTransaction = createNewAccountTransaction(defaultAccountId, usd(10.0))
        when:
            Transaction withdrawTransaction = initialTransaction.nextWithdrawTransaction(usd(withdrawAmount))
        then:
            with(withdrawTransaction) {
                accountId == defaultAccountId
                amount == usd(withdrawAmount).negate()
                previousId == initialTransaction.id
                id == initialTransaction.id.nextId()
            }
        where:
            withdrawAmount | currency
            5.0            | 'GBP'
            -5.0           | 'USD'
            0.0            | 'USD'
    }

    def "Should throw NullPointerException when amount is null on nextWithdrawTransaction"() {
        given:
            Transaction initialTransaction = createNewAccountTransaction(defaultAccountId, usd(10.0))
        when:
            initialTransaction.nextWithdrawTransaction(null)
        then:
            thrown(NullPointerException)
    }

    def "Should create deposit transaction with #depositAmount #currency amount"() {
        given:
            Transaction initialTransaction = createNewAccountTransaction(defaultAccountId, usd(10.0))
        when:
            Transaction depositTransaction = initialTransaction.nextDepositTransaction(usd(depositAmount))
        then:
            with(depositTransaction) {
                accountId == defaultAccountId
                amount == usd(depositAmount)
                previousId == initialTransaction.id
                id == initialTransaction.id.nextId()
            }
        where:
            depositAmount | currency
            5.0           | 'GBP'
            -5.0          | 'USD'
            0.0           | 'USD'
    }

    def "Should throw NullPointerException when amount is null on nextDepositTransaction"() {
        given:
            Transaction initialTransaction = createNewAccountTransaction(defaultAccountId, usd(10.0))
        when:
            initialTransaction.nextDepositTransaction(null)
        then:
            thrown(NullPointerException)
    }
}
