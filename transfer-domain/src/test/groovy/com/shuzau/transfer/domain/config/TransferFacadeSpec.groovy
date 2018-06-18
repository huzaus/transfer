package com.shuzau.transfer.domain.config

import com.shuzau.transfer.domain.primary.TransferFacade
import com.shuzau.transfer.domain.transaction.AccountId
import com.shuzau.transfer.domain.transfer.TransferId
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Subject

import static com.shuzau.transfer.domain.config.TransferDomainConfiguration.inMemoryTransferFacade
import static com.shuzau.transfer.domain.core.Money.gbp
import static com.shuzau.transfer.domain.core.Money.usd
import static com.shuzau.transfer.domain.transfer.TransferStatus.COMPLETED
import static com.shuzau.transfer.domain.transfer.TransferStatus.FAILED

class TransferFacadeSpec extends Specification {
    @Shared
    @Subject
    TransferFacade transferFacade = inMemoryTransferFacade()

    def "Should return empty Optional for non-existent transferId"() {
        given:
            TransferId transferId = TransferId.of(1_000L)
        when:
            Optional transfer = transferFacade.findTransferById(transferId)
        then:
            !transfer.isPresent()
    }

    def "Should submit and execute transfer for #money amount"() {
        given:
            AccountId source = transferFacade.createAccountWithBalance(sourceInitBalance).getId()
            AccountId target = transferFacade.createAccountWithBalance(targetInitBalabce).getId()
            TransferId id = transferFacade.submitTransfer(source, target, money)
        when:
            Optional transfer = transferFacade.findTransferById(id)
        then:
            with(transfer.get()) {
                transferId == id
                sourceAccount == source
                targetAccount == target
                amount == money
                status == COMPLETED
            }
        and:
            transferFacade.findAccountById(source).get().balance == sourceExpectedBalance
        and:
            transferFacade.findAccountById(target).get().balance == targetExpectedBalabce
        where:
            sourceInitBalance | targetInitBalabce | money     || sourceExpectedBalance | targetExpectedBalabce
            usd(100.0)        | usd(10.0)         | usd(10.0) || usd(90.0)             | usd(20.0)
    }

    def "Should submit and fail for #money amount"() {
        given:
            AccountId source = transferFacade.createAccountWithBalance(sourceInitBalance).getId()
            AccountId target = transferFacade.createAccountWithBalance(targetInitBalabce).getId()
            TransferId id = transferFacade.submitTransfer(source, target, money)
        when:
            Optional transfer = transferFacade.findTransferById(id)
        then:
            with(transfer.get()) {
                transferId == id
                sourceAccount == source
                targetAccount == target
                amount == money
                status == FAILED
            }
        and:
            transferFacade.findAccountById(source).get().balance == sourceInitBalance
        and:
            transferFacade.findAccountById(target).get().balance == targetInitBalabce
        where:
            sourceInitBalance | targetInitBalabce | money
            usd(100.0)        | gbp(10.0)         | usd(10.0)
    }
}