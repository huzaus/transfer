package com.shuzau.transfer.domain.config

import com.shuzau.transfer.domain.secondary.TransferEventBus
import com.shuzau.transfer.domain.transfer.TransferCompletedEvent
import com.shuzau.transfer.domain.transfer.TransferCreatedEvent
import com.shuzau.transfer.domain.transfer.TransferEvent
import com.shuzau.transfer.domain.transfer.TransferFailedEvent
import io.reactivex.Observable
import io.reactivex.observers.TestObserver
import spock.lang.Specification
import spock.lang.Subject
import spock.lang.Unroll

import static com.shuzau.transfer.domain.transfer.DefaultTransferEventFactory.defaultCompletedEvent
import static com.shuzau.transfer.domain.transfer.DefaultTransferEventFactory.defaultCreatedEvent
import static com.shuzau.transfer.domain.transfer.DefaultTransferEventFactory.defaultFailedEvent

@Unroll
class LocalTransferEventBusSpec extends Specification {

    @Subject
    TransferEventBus transferEventBus = new LocalTransferEventBus()

    def "Subscription to #eventType should be notified about #event on publish"() {
        given:
            Observable observable = transferEventBus.observe(eventType)
            TestObserver observer = TestObserver.create()
            observable.subscribe(observer)
        when:
            transferEventBus.publish(event)
        then:
            observer.values() == [event]
        where:
            eventType              | event
            TransferEvent          | defaultCompletedEvent()
            TransferEvent          | defaultFailedEvent()
            TransferEvent          | defaultCreatedEvent()
            TransferCompletedEvent | defaultCompletedEvent()
            TransferFailedEvent    | defaultFailedEvent()
            TransferCreatedEvent   | defaultCreatedEvent()
    }

    def "Subscription to #eventType should not be notified about #event on publish"() {
        given:
            Observable observable = transferEventBus.observe(eventType)
            TestObserver observer = TestObserver.create()
            observable.subscribe(observer)
        when:
            transferEventBus.publish(event)
        then:
            observer.assertEmpty()
        where:
            eventType              | event
            TransferCompletedEvent | defaultFailedEvent()
            TransferFailedEvent    | defaultCreatedEvent()
            TransferCreatedEvent   | defaultFailedEvent()
    }

    def "Subscriber to #eventType should be notified only about #events on publish"() {
        given:
            Observable observable = transferEventBus.observe(eventType)
            TestObserver observer = TestObserver.create()
            observable.subscribe(observer)
        when:
            allEvent.forEach({ transferEventBus.publish(it) })
        then:
            observer.values() == events
        where:
            eventType              | allEvent                                                               || events
            TransferCompletedEvent | [defaultCreatedEvent(), defaultCompletedEvent(), defaultFailedEvent()] || [defaultCompletedEvent()]
            TransferFailedEvent    | [defaultCreatedEvent(), defaultFailedEvent(), defaultCompletedEvent()] || [defaultFailedEvent()]
            TransferEvent          | [defaultCreatedEvent(), defaultCompletedEvent(), defaultFailedEvent()] || [defaultCreatedEvent(), defaultCompletedEvent(), defaultFailedEvent()]
            TransferCreatedEvent   | [defaultCreatedEvent(), defaultCompletedEvent(), defaultFailedEvent()] || [defaultCreatedEvent()]
    }
}
