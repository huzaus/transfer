package com.shuzau.transfer.domain.transfer

import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class TransferCompletedEventSpec extends Specification {

    def "Should throw NullPointerException when transferId = #transferId "() {
        when:
            TransferCompletedEvent.of(transferId)
        then:
            thrown(NullPointerException)
        where:
            transferId = null
    }

    def "Should create TransferCompletedEvent with #expectedTransferId "() {
        when:
            TransferCompletedEvent event = TransferCompletedEvent.of(expectedTransferId)
        then:
            event.transferId == expectedTransferId
        where:
            expectedTransferId = TransferId.of(1L)
    }
}
