package com.shuzau.transfer.domain.config

import com.shuzau.transfer.domain.primary.TransferRegistry
import com.shuzau.transfer.domain.transaction.AccountId
import com.shuzau.transfer.domain.transfer.TransferId
import spock.lang.Specification
import spock.lang.Subject

import static com.shuzau.transfer.domain.config.TransferDomainConfiguration.inMemoryTransferFacade
import static com.shuzau.transfer.domain.core.Money.usd
import static com.shuzau.transfer.domain.transfer.TransferStatus.CREATED

class TransferRegistrySpec extends Specification {
    @Subject
    TransferRegistry transferRegistry = inMemoryTransferFacade()

    def "Should return empty Optional for non-existent transferId"() {
        given:
            TransferId transferId = TransferId.of(1_000L)
        when:
            Optional transfer = transferRegistry.findTransferById(transferId)
        then:
            !transfer.isPresent()
    }

    def "Should return submitted transfer with expected values for #transferId"() {
        given:
            TransferId id = transferRegistry.submitTransfer(source, target, money)
        when:
            Optional transfer = transferRegistry.findTransferById(id)
        then:
            with(transfer.get()) {
                transferId == id
                sourceAccount == source
                targetAccount == target
                amount == money
                status == CREATED
            }
        where:
            source           | target           | money
            AccountId.of(1L) | AccountId.of(2L) | usd(100.0)
    }

}
