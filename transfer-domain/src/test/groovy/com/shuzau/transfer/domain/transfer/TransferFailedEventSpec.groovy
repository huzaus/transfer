package com.shuzau.transfer.domain.transfer

import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class TransferFailedEventSpec extends Specification {

    def "Should throw NullPointerException when transferId = #transferId and reason = #reason"() {
        when:
            TransferFailedEvent.of(transferId, reason)
        then:
            thrown(NullPointerException)
        where:
            transferId        | reason
            null              | "Source account doesn't exist"
            TransferId.of(1L) | null
    }

    def "Should create TransferFailedEvent with #expectedTransferId and #expectedReason"() {
        when:
            TransferFailedEvent event = TransferFailedEvent.of(expectedTransferId, expectedReason)
        then:
            with(event) {
                transferId == expectedTransferId
                reason == expectedReason

            }
        where:
            expectedTransferId | expectedReason
            TransferId.of(1L)  | "Source account doesn't exist"
    }
}
