package com.shuzau.transfer.domain.core

import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

import static com.shuzau.transfer.domain.core.Money.gbp
import static com.shuzau.transfer.domain.core.Money.usd

@Unroll
class TransactionSpec extends Specification {
    @Shared
    private defaultAccountId = AccountId.of(1L)

    def "Should create transaction for new account with #balance balance"() {
        given:
            Transaction transaction = Transaction.createNewAccountTransaction(newAccountId, balance)
                                                 .withId(transactionId)
        expect:
            with(transaction) {
                accountId == newAccountId
                amount == balance
                initial == true
            }
        where:
            transactionId         | newAccountId     | balance
            TransactionId.of(10L) | AccountId.of(1L) | usd(10.0)
            TransactionId.of(11L) | AccountId.of(2L) | gbp(-10.0)
    }

    def "Should throw NullPointerException when transactionId == #transactionId, balance == #balance account id == #accountId on createNewAccountTransaction"() {
        when:
            Transaction.createNewAccountTransaction(accountId, balance)
                       .withId(transactionId)
        then:
            thrown(NullPointerException)
        where:
            transactionId        | accountId        | balance
            null                 | AccountId.of(1L) | usd(10.0)
            TransactionId.of(1L) | null             | usd(10.0)
            TransactionId.of(1L) | AccountId.of(1L) | null
    }

    def "Should create withdraw transaction with #withdrawAmount #currency amount"() {
        given:
            Transaction initialTransaction = Transaction.createNewAccountTransaction(defaultAccountId, usd(10.0))
                                                        .withId(TransactionId.of(1L))

        when:
            Transaction withdrawTransaction = initialTransaction.nextWithdrawTransaction(usd(withdrawAmount))
                                                                .withId(TransactionId.of(2L))

        then:
            with(withdrawTransaction) {
                accountId == defaultAccountId
                amount == usd(withdrawAmount).negate()
                previousTransaction == initialTransaction
            }
        where:
            withdrawAmount | currency
            5.0            | 'GBP'
            -5.0           | 'USD'
            0.0            | 'USD'
    }

    def "Should throw NullPointerException when amount is null on nextWithdrawTransaction"() {
        given:
            Transaction initialTransaction = Transaction.createNewAccountTransaction(defaultAccountId, usd(10.0))
                                                        .withId(TransactionId.of(1L))

        when:
            initialTransaction.nextWithdrawTransaction(null)
                              .withId(TransactionId.of(2L))
        then:
            thrown(NullPointerException)
    }

    def "Should create deposit transaction with #depositAmount #currency amount"() {
        given:
            Transaction initialTransaction = Transaction.createNewAccountTransaction(defaultAccountId, usd(10.0))
                                                        .withId(TransactionId.of(1L))
        when:
            Transaction depositTransaction = initialTransaction.nextDepositTransaction(usd(depositAmount))
                                                               .withId(TransactionId.of(2L))
        then:
            with(depositTransaction) {
                accountId == defaultAccountId
                amount == usd(depositAmount)
                previousTransaction == initialTransaction
            }
        where:
            depositAmount | currency
            5.0           | 'GBP'
            -5.0          | 'USD'
            0.0           | 'USD'
    }

    def "Should throw NullPointerException when amount is null on nextDepositTransaction"() {
        given:
            Transaction initialTransaction = Transaction.createNewAccountTransaction(defaultAccountId, usd(10.0))
                                                        .withId(TransactionId.of(1L))
        when:
            initialTransaction.nextDepositTransaction(null)
        then:
            thrown(NullPointerException)
    }
}
