package com.shuzau.transfer.domain.transfer

import spock.lang.Specification

class TransferIdSpec extends Specification {

    def "Should create TransferId with provided value"() {
        when:
            TransferId transactionId = TransferId.of(id)
        then:
            transactionId.id == id
        where:
            id = 1_650L
    }

    def "Should throw NullPointerException when id is null"() {
        when:
            TransferId.of(null)
        then:
            thrown(NullPointerException)
    }
}
