package com.shuzau.transfer.domain.transfer

import com.shuzau.transfer.domain.exception.TransferException
import com.shuzau.transfer.domain.transaction.AccountId
import spock.lang.Specification
import spock.lang.Unroll

import static com.shuzau.transfer.domain.core.Money.gbp
import static com.shuzau.transfer.domain.core.Money.usd
import static DefaultTransferEventFactory.defaultCompletedEvent
import static DefaultTransferEventFactory.defaultCreatedEvent
import static DefaultTransferEventFactory.defaultFailedEvent
import static com.shuzau.transfer.domain.transfer.TransferStatus.COMPLETED
import static com.shuzau.transfer.domain.transfer.TransferStatus.CREATED
import static com.shuzau.transfer.domain.transfer.TransferStatus.FAILED

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
                status == CREATED
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
            transfer.status == expectedState
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

    def "Should throw TransferException when applying #events"() {
        when:
            Transfer.build(events)
        then:
            thrown(TransferException)
        where:
            events                                                                                                                    | _
            []                                                                                                                        | _
            [defaultCompletedEvent(TransferId.of(1L))]                                                                                | _
            [defaultFailedEvent(TransferId.of(1L))]                                                                                   | _
            [defaultCreatedEvent(TransferId.of(1L)), defaultCompletedEvent(TransferId.of(2L))]                                        | _
            [defaultCreatedEvent(TransferId.of(1L)), defaultCompletedEvent(TransferId.of(1L)), defaultFailedEvent(TransferId.of(1L))] | _
    }

    def "Should build #transfer from TransferCreatedEvent with expected values"() {
        given:
            TransferCreatedEvent transferCreatedEvent = TransferCreatedEvent.builder()
                                                                            .transferId(id)
                                                                            .sourceAccount(source)
                                                                            .targetAccount(target)
                                                                            .amount(money)
                                                                            .build()
        when:
            Transfer transfer = Transfer.build([transferCreatedEvent])
        then:
            with(transfer) {
                transferId == id
                sourceAccount == source
                targetAccount == target
                amount == money
                status == CREATED
            }
        where:
            id                | source           | target           | money
            TransferId.of(1L) | AccountId.of(1L) | AccountId.of(2L) | usd(100.0)
            TransferId.of(1L) | AccountId.of(1L) | AccountId.of(2L) | gbp(0.0)
            TransferId.of(1L) | AccountId.of(1L) | AccountId.of(2L) | usd(-100.0)
    }

    def "Should build Transfer from and set status to #expectedStatus"() {
        given:
            TransferCreatedEvent transferCreatedEvent = defaultCreatedEvent(id)
        when:
            Transfer transfer = Transfer.build([transferCreatedEvent, event])
        then:
            with(transfer) {
                transferId == id
                sourceAccount == transferCreatedEvent.sourceAccount
                targetAccount == transferCreatedEvent.targetAccount
                amount == transferCreatedEvent.amount
                status == expectedStatus
            }
        where:
            id                | event                     | expectedStatus
            TransferId.of(1L) | defaultCompletedEvent(id) | COMPLETED
            TransferId.of(1L) | defaultFailedEvent(id)    | FAILED
    }
}