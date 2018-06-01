package com.shuzau.transfer.domain.transfer

import com.shuzau.transfer.domain.transaction.AccountId
import spock.lang.Specification
import spock.lang.Unroll

import static com.shuzau.transfer.domain.core.Money.usd
import static com.shuzau.transfer.domain.transfer.TransferState.COMPLETED
import static com.shuzau.transfer.domain.transfer.TransferState.CREATED
import static com.shuzau.transfer.domain.transfer.TransferState.FAILED

@Unroll
class TransferSpec extends Specification {

    def "Should throw NullPointerException when TransferCreatedEvent is null on create"() {
        given:
            TransferCreatedEvent event = null
        when:
            Transfer.create(event)
        then:
            thrown(NullPointerException)
    }

    def "Should create Transfer from TransferCreatedEvent with expected values"() {
        given:
            TransferCreatedEvent transferCreatedEvent = TransferCreatedEvent.builder()
                                                                            .transferId(expectedTransferId)
                                                                            .sourceAccount(expectedSourceAccount)
                                                                            .targetAccount(excpectedTargetAccount)
                                                                            .amount(expectedAmount)
                                                                            .build()

        when:
            Transfer transfer = Transfer.create(transferCreatedEvent)
        then:
            with(transfer) {
                transferId == expectedTransferId
                sourceAccount == expectedSourceAccount
                targetAccount == excpectedTargetAccount
                amount == expectedAmount
                state == CREATED
            }
        where:
            expectedTransferId | expectedSourceAccount | excpectedTargetAccount | expectedAmount
            TransferId.of(1L)  | AccountId.of(1L)      | AccountId.of(2L)       | usd(100.0)
    }

    def "Should change Transfer status to COMPLETED on applying TransferCompletedEvent"() {
        given:
            TransferCreatedEvent transferCreatedEvent = TransferCreatedEvent.builder()
                                                                            .transferId(transferId)
                                                                            .sourceAccount(sourceAccount)
                                                                            .targetAccount(targetAccount)
                                                                            .amount(amount)
                                                                            .build()

        and:
            Transfer transfer = Transfer.create(transferCreatedEvent)
        when:
            transfer.apply(TransferCompletedEvent.of(transferId))
        then:
            transfer.state == COMPLETED
        where:
            transferId        | sourceAccount    | targetAccount    | amount
            TransferId.of(1L) | AccountId.of(1L) | AccountId.of(2L) | usd(100.0)
    }

    def "Should change Transfer status to Failed on applying TransferFailedEvent"() {
        given:
            TransferCreatedEvent transferCreatedEvent = TransferCreatedEvent.builder()
                                                                            .transferId(transferId)
                                                                            .sourceAccount(sourceAccount)
                                                                            .targetAccount(targetAccount)
                                                                            .amount(amount)
                                                                            .build()

        and:
            Transfer transfer = Transfer.create(transferCreatedEvent)
        when:
            transfer.apply(TransferFailedEvent.of(transferId, "Reason"))
        then:
            transfer.state == FAILED
        where:
            transferId        | sourceAccount    | targetAccount    | amount
            TransferId.of(1L) | AccountId.of(1L) | AccountId.of(2L) | usd(100.0)
    }
}
