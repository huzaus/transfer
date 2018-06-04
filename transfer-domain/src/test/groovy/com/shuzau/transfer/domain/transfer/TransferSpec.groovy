package com.shuzau.transfer.domain.transfer

import com.shuzau.transfer.domain.exception.TransferException
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

    def "Should change Transfer status to #expectedState on applying #event"() {
        given:
            TransferCreatedEvent transferCreatedEvent = TransferCreatedEvent.builder()
                                                                            .transferId(transferId)
                                                                            .sourceAccount(AccountId.of(1L))
                                                                            .targetAccount(AccountId.of(2L))
                                                                            .amount(usd(100.0))
                                                                            .build()

        and:
            Transfer transfer = Transfer.create(transferCreatedEvent)
        when:
            transfer.apply(event)
        then:
            transfer.state == expectedState
        where:
            transferId        | event                                        || expectedState
            TransferId.of(1L) | TransferCompletedEvent.of(transferId)        || COMPLETED
            TransferId.of(1L) | TransferFailedEvent.of(transferId, "Reason") || FAILED
    }

    def "Should throw TransferException when applying #event on Completed transfer"() {
        given:
            TransferCreatedEvent transferCreatedEvent = TransferCreatedEvent.builder()
                                                                            .transferId(transferId)
                                                                            .sourceAccount(AccountId.of(1L))
                                                                            .targetAccount(AccountId.of(2L))
                                                                            .amount(usd(100.0))
                                                                            .build()

        and:
            Transfer transfer = Transfer.create(transferCreatedEvent)
                                        .apply(TransferCompletedEvent.of(transferId))
        when:
            transfer.apply(event)
        then:
            thrown(TransferException)
        where:
            transferId        | event
            TransferId.of(1L) | TransferCompletedEvent.of(transferId)
            TransferId.of(1L) | TransferFailedEvent.of(transferId, "Reason")
    }

    def "Should throw TransferException when applying #event on Failed Transfer"() {
        given:
            TransferCreatedEvent transferCreatedEvent = TransferCreatedEvent.builder()
                                                                            .transferId(transferId)
                                                                            .sourceAccount(AccountId.of(1L))
                                                                            .targetAccount(AccountId.of(2L))
                                                                            .amount(usd(100.0))
                                                                            .build()

        and:
            Transfer transfer = Transfer.create(transferCreatedEvent)
                                        .apply(TransferFailedEvent.of(transferId, 'Reason'))
        when:
            transfer.apply(event)
        then:
            thrown(TransferException)
        where:
            transferId        | event
            TransferId.of(1L) | TransferCompletedEvent.of(transferId)
            TransferId.of(1L) | TransferFailedEvent.of(transferId, "Reason")
    }

    def "Should throw TransferException when applying #event with different transferId"() {
        given:
            TransferCreatedEvent transferCreatedEvent = TransferCreatedEvent.builder()
                                                                            .transferId(transferId)
                                                                            .sourceAccount(AccountId.of(1L))
                                                                            .targetAccount(AccountId.of(2L))
                                                                            .amount(usd(100.0))
                                                                            .build()

        and:
            Transfer transfer = Transfer.create(transferCreatedEvent)
        when:
            transfer.apply(event)
        then:
            thrown(TransferException)
        where:
            transferId        | eventTransferId   | event
            TransferId.of(1L) | TransferId.of(2L) | TransferCompletedEvent.of(eventTransferId)
            TransferId.of(1L) | TransferId.of(2L) | TransferFailedEvent.of(eventTransferId, 'Reason')
    }
}