package com.shuzau.transfer.domain.entities

import spock.lang.Specification

class TransactionIdSpec extends Specification {

    def "Should create initial Id"() {
        given:
            TransactionId initialId = TransactionId.initial()
        expect:
            initialId.id == 0L
            initialId.initial
    }

    def "Should create Id with incremented value"() {
        given:
            TransactionId initialId = TransactionId.initial()
        when:
            TransactionId transactionId = initialId.nextId()
        then:
            transactionId.id == 1L
            !transactionId.initial
    }
}
