package com.shuzau.transfer.domain.transfer

import com.shuzau.transfer.domain.transaction.AccountId
import spock.lang.Specification
import spock.lang.Unroll

import static com.shuzau.transfer.domain.core.Money.usd

@Unroll
class TransferCreatedEventSpec extends Specification {

    def "Should throw NullPointerException when transferId,sourceAccount,targetAccount or amount is null"() {
        when:
            TransferCreatedEvent.builder()
                                .transferId(transferId)
                                .sourceAccount(sourceAccount)
                                .targetAccount(targetAccount)
                                .amount(amount)
                                .build()
        then:
            thrown(NullPointerException)
        where:
            transferId        | sourceAccount    | targetAccount    | amount
            null              | AccountId.of(1L) | AccountId.of(2L) | usd(100.0)
            TransferId.of(1L) | null             | AccountId.of(2L) | usd(100.0)
            TransferId.of(1L) | AccountId.of(1L) | null             | usd(100.0)
            TransferId.of(1L) | AccountId.of(1L) | AccountId.of(2L) | null
    }

    def "Should create TransferFailedEvent with #expectedTransferId, #expectedSourceAccount, #expectedTargetAccount  and #expectedAmount"() {
        when:
            TransferCreatedEvent event = TransferCreatedEvent.builder()
                                                             .transferId(expectedTransferId)
                                                             .sourceAccount(expectedSourceAccount)
                                                             .targetAccount(expectedTargetAccount)
                                                             .amount(expectedAmount)
                                                             .build()
        then:
            with(event) {
                transferId == expectedTransferId
                sourceAccount == expectedSourceAccount
                targetAccount == expectedTargetAccount
                amount == expectedAmount

            }
        where:
            expectedTransferId | expectedSourceAccount | expectedTargetAccount | expectedAmount
            TransferId.of(1L)  | AccountId.of(1L)      | AccountId.of(2L)      | usd(100.0)
    }
}
