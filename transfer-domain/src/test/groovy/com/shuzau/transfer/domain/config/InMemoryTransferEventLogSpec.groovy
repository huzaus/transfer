package com.shuzau.transfer.domain.config

import com.shuzau.transfer.domain.secondary.TransferEventLog
import com.shuzau.transfer.domain.transfer.TransferCompletedEvent
import com.shuzau.transfer.domain.transfer.TransferId
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Subject
import spock.lang.Unroll

import static com.shuzau.transfer.domain.transfer.DefaultTransferEventFactory.defaultCreatedEvent
import static com.shuzau.transfer.domain.transfer.DefaultTransferEventFactory.defaultFailedEvent

@Unroll
class InMemoryTransferEventLogSpec extends Specification {

    @Subject
    @Shared
    TransferEventLog transferEventLog = new InMemoryTransferEventLog()

    def "Should return unique transferId on nextTransferId"() {
        given:
            TransferId transferId = transferEventLog.nextTransferId()
        when:
            TransferId nextTransferId = transferEventLog.nextTransferId()
        then:
            nextTransferId.id != transferId.id
    }

    def "Should return #expectedEvents for a given #transferId"() {
        given:
            events.forEach({ transferEventLog.store(it) })
        when:
            List storedEvents = transferEventLog.findEvents(transferId)
        then:
            storedEvents == expectedEvents
        where:
            transferId                        | nextTransferId                    | events                                                                                                 || expectedEvents
            transferEventLog.nextTransferId() | transferEventLog.nextTransferId() | []                                                                                                     || []
            transferEventLog.nextTransferId() | transferEventLog.nextTransferId() | [defaultCreatedEvent(nextTransferId)]                                                                  || []
            transferEventLog.nextTransferId() | transferEventLog.nextTransferId() | [defaultCreatedEvent(transferId)]                                                                      || [defaultCreatedEvent(transferId)]
            transferEventLog.nextTransferId() | transferEventLog.nextTransferId() | [defaultCreatedEvent(transferId), defaultFailedEvent(transferId)]                                      || [defaultCreatedEvent(transferId), defaultFailedEvent(transferId)]
            transferEventLog.nextTransferId() | transferEventLog.nextTransferId() | [defaultCreatedEvent(transferId), defaultCreatedEvent(nextTransferId), defaultFailedEvent(transferId)] || [defaultCreatedEvent(transferId), defaultFailedEvent(transferId)]
            transferEventLog.nextTransferId() | transferEventLog.nextTransferId() | [defaultCreatedEvent(transferId), TransferCompletedEvent.of(transferId)]                               || [defaultCreatedEvent(transferId), TransferCompletedEvent.of(transferId)]
    }


}