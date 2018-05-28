package com.shuzau.transfer.domain.transaction

import com.shuzau.transfer.domain.transfer.TransferId
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
                previousTransaction == null
                transferId == null
            }
        where:
            transactionId         | newAccountId     | balance
            TransactionId.of(10L) | AccountId.of(1L) | usd(10.0)
            TransactionId.of(11L) | AccountId.of(2L) | gbp(-10.0)
            TransactionId.of(12L) | AccountId.of(3L) | gbp(0.0)
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

    def "Should create transaction with #withdrawAmount USD amount"() {
        given:
            Transaction initialTransaction = Transaction.createNewAccountTransaction(defaultAccountId, usd(10.0))
                                                        .withId(TransactionId.of(1L))

        when:
            Transaction withdrawTransaction = initialTransaction.nextTransaction(usd(withdrawAmount))
                                                                .withId(TransactionId.of(2L))

        then:
            with(withdrawTransaction) {
                accountId == defaultAccountId
                amount == usd(withdrawAmount)
                previousTransaction == initialTransaction
                transferId == null
            }
        where:
            withdrawAmount | _
            5.0            | _
            -5.0           | _
            0.0            | _
    }

    def "Should throw NullPointerException when transactionId or amount is null on nextTransaction"() {
        given:
            Transaction initialTransaction = Transaction.createNewAccountTransaction(defaultAccountId, usd(10.0))
                                                        .withId(TransactionId.of(1L))

        when:
            initialTransaction.nextTransaction(amount)
                              .withId(transactionId)
        then:
            thrown(NullPointerException)
        where:
            transactionId        | amount
            null                 | usd(5.0)
            TransactionId.of(2L) | null
    }

    def "Should create transfer transaction with #withdrawAmount USD amount and #transferId"() {
        given:
            Transaction initialTransaction = Transaction.createNewAccountTransaction(defaultAccountId, usd(10.0))
                                                        .withId(TransactionId.of(1L))

        when:
            Transaction withdrawTransaction = initialTransaction.nextTransferTransaction(transferId, usd(withdrawAmount))
                                                                .withId(TransactionId.of(2L))

        then:
            with(withdrawTransaction) {
                accountId == defaultAccountId
                amount == usd(withdrawAmount)
                previousTransaction == initialTransaction
                transferId == transferId
            }
        where:
            withdrawAmount | transferId
            5.0            | TransferId.of(1L)
            -5.0           | TransferId.of(2L)
            0.0            | TransferId.of(3L)
    }

    def "Should throw NullPointerException when transactionId, transferId or amount is null on nextTransferTransaction"() {
        given:
            Transaction initialTransaction = Transaction.createNewAccountTransaction(defaultAccountId, usd(10.0))
                                                        .withId(TransactionId.of(1L))

        when:
            initialTransaction.nextTransferTransaction(transferId, amount)
                              .withId(transactionId)
        then:
            thrown(NullPointerException)
        where:
            transactionId        | transferId        | amount
            null                 | TransferId.of(1L) | usd(10.0)
            TransactionId.of(1L) | null              | usd(10.0)
            TransactionId.of(1L) | TransferId.of(1L) | null
    }

}
