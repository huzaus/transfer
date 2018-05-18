package com.shuzau.transfer.domain.transaction

import spock.lang.Specification

class TransactionIdSpec extends Specification {

    def "Should create TransactionId with provided value"() {
        when:
            TransactionId transactionId = TransactionId.of(id)
        then:
            transactionId.id == id
        where:
            id = 1_650L
    }

    def "Should throw NullPointerException when id is null"() {
        when:
            TransactionId.of(null)
        then:
            thrown(NullPointerException)
    }
}
