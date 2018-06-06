package com.shuzau.transfer.domain.config

import com.shuzau.transfer.domain.secondary.TransferEventPublisher
import com.shuzau.transfer.domain.transaction.AccountId
import com.shuzau.transfer.domain.transfer.TransferCompletedEvent
import com.shuzau.transfer.domain.transfer.TransferCreatedEvent
import com.shuzau.transfer.domain.transfer.TransferEvent
import com.shuzau.transfer.domain.transfer.TransferEventSubscriber
import com.shuzau.transfer.domain.transfer.TransferFailedEvent
import com.shuzau.transfer.domain.transfer.TransferId
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Subject
import spock.lang.Unroll

import static com.shuzau.transfer.domain.core.Money.usd

@Unroll
class LocalTransferEventPublisherTestSpec extends Specification {

    @Subject
    TransferEventPublisher transferEventPublisher = new LocalTransferEventPublisher()
    @Shared
    TransferCreatedEvent transferCreatedEvent = TransferCreatedEvent.builder()
                                                                    .transferId(TransferId.of(1L))
                                                                    .sourceAccount(AccountId.of(1L))
                                                                    .targetAccount(AccountId.of(2L))
                                                                    .amount(usd(100.0))
                                                                    .build()

    def "Subscriber of #eventType should be notified about #event on publish"() {
        given:
            TransferEventSubscriber subscriber = Mock()
        and:
            transferEventPublisher.subscribe(eventType, subscriber)
        when:
            transferEventPublisher.publish(event)
        then:
            1 * subscriber.onEvent(event)
        where:
            eventType              | event
            TransferEvent          | TransferCompletedEvent.of(TransferId.of(1L))
            TransferEvent          | TransferFailedEvent.of(TransferId.of(1L), "Hello")
            TransferEvent          | transferCreatedEvent
            TransferCompletedEvent | TransferCompletedEvent.of(TransferId.of(1L))
            TransferFailedEvent    | TransferFailedEvent.of(TransferId.of(1L), "Hello")
            TransferCreatedEvent   | transferCreatedEvent
    }

    def "Subscriber with #eventType should not be notified about #event on publish"() {
        given:
            TransferEventSubscriber subscriber = Mock()
        and:
            transferEventPublisher.subscribe(eventType, subscriber)
        when:
            transferEventPublisher.publish(event)
        then:
            0 * subscriber.onEvent(event)
        where:
            eventType              | event
            TransferCompletedEvent | TransferFailedEvent.of(TransferId.of(1L), "Hello")
            TransferFailedEvent    | transferCreatedEvent
            TransferCreatedEvent   | TransferCompletedEvent.of(TransferId.of(1L))
    }
}
