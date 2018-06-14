package com.shuzau.transfer.domain.config

import com.shuzau.transfer.domain.primary.AccountRegistry
import com.shuzau.transfer.domain.transaction.AccountId
import com.shuzau.transfer.domain.transfer.TransferCompletedEvent
import com.shuzau.transfer.domain.transfer.TransferCreatedEvent
import com.shuzau.transfer.domain.transfer.TransferEvent
import com.shuzau.transfer.domain.transfer.TransferFailedEvent
import com.shuzau.transfer.domain.transfer.TransferId
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Subject
import spock.lang.Unroll

import static com.shuzau.transfer.domain.config.TransferDomainConfiguration.inMemoryTransferFacade
import static com.shuzau.transfer.domain.core.Money.gbp
import static com.shuzau.transfer.domain.core.Money.usd

@Unroll
class TransferCreatedProcessorSpec extends Specification {

    @Shared
    AccountRegistry accountRegistry = inMemoryTransferFacade()
    @Shared
    @Subject
    TransferCreatedProcessor transferCreatedProcessor = new TransferCreatedProcessor(accountRegistry)

    def "Should return TransferCompletedEvent for #transferId"() {
        given:
            AccountId sourceAccount = accountRegistry.createAccountWithBalance(sourceInitBalance).getId()
            AccountId targetAccount = accountRegistry.createAccountWithBalance(targetInitBalance).getId()
        and:
            TransferCreatedEvent event = TransferCreatedEvent.builder()
                                                             .sourceAccount(sourceAccount)
                                                             .targetAccount(targetAccount)
                                                             .transferId(transferId)
                                                             .amount(amount)
                                                             .build()
        when:
            TransferEvent result = transferCreatedProcessor.process(event)
        then:
            result == TransferCompletedEvent.of(transferId)
        and:
            accountRegistry.findAccountById(sourceAccount).get().getBalance() == sourceResultBalance
            accountRegistry.findAccountById(targetAccount).get().getBalance() == targetResultBalance
        where:
            sourceInitBalance | targetInitBalance | transferId        | amount     || sourceResultBalance | targetResultBalance
            usd(30.0)         | usd(100.0)        | TransferId.of(1L) | usd(20.0)  || usd(10.0)           | usd(120.0)
            usd(20.0)         | usd(-100.0)       | TransferId.of(2L) | usd(20.0)  || usd(0.0)            | usd(-80.0)
            usd(20.0)         | usd(20.0)         | TransferId.of(3L) | usd(0.0)   || usd(20.0)           | usd(20.0)
            usd(10.0)         | usd(10.0)         | TransferId.of(4L) | usd(-10.0) || usd(20.0)           | usd(00.0)
    }

    def "Should return TransferFailedEvent with #reason"() {
        given:
            AccountId sourceAccount = accountRegistry.createAccountWithBalance(sourceInitBalance).getId()
            AccountId targetAccount = accountRegistry.createAccountWithBalance(targetInitBalance).getId()
        and:
            TransferCreatedEvent event = TransferCreatedEvent.builder()
                                                             .sourceAccount(sourceAccount)
                                                             .targetAccount(targetAccount)
                                                             .transferId(transferId)
                                                             .amount(amount)
                                                             .build()
        when:
            TransferEvent result = transferCreatedProcessor.process(event)
        then:
            result == TransferFailedEvent.of(transferId, reason)
        where:
            sourceInitBalance | targetInitBalance | transferId        | amount    || reason
            gbp(30.0)         | usd(100.0)        | TransferId.of(1L) | usd(20.0) || "Can't operate on different currencies: GBP and USD"
            usd(50.0)         | gbp(100.0)        | TransferId.of(2L) | usd(30.0) || "Can't operate on different currencies: GBP and USD"
            usd(50.0)         | usd(100.0)        | TransferId.of(2L) | gbp(30.0) || "Can't operate on different currencies: USD and GBP"
            usd(20.0)         | usd(100.0)        | TransferId.of(3L) | usd(30.0) || "Insufficient funds: Money(amount=-10.0, currency=USD)"
            usd(20.0)         | usd(-100.0)       | TransferId.of(3L) | usd(50.0) || "Insufficient funds: Money(amount=-30.0, currency=USD)"
    }

    def "Should return TransferFailedEvent when source account does not exist #reason"() {
        given:
            AccountId targetAccount = accountRegistry.createAccountWithBalance(usd(100.0)).getId()
            TransferCreatedEvent event = TransferCreatedEvent.builder()
                                                             .sourceAccount(accountId)
                                                             .targetAccount(targetAccount)
                                                             .transferId(transferId)
                                                             .amount(usd(10.0))
                                                             .build()
        when:
            TransferEvent result = transferCreatedProcessor.process(event)
        then:
            result == TransferFailedEvent.of(transferId, reason)
        where:
            accountId           | transferId        || reason
            AccountId.of(1_000) | TransferId.of(4L) || "$accountId doesn't exist"
    }

    def "Should return TransferFailedEvent when target account does not exist #reason"() {
        given:
            AccountId sourceAccount = accountRegistry.createAccountWithBalance(usd(100.0)).getId()
            TransferCreatedEvent event = TransferCreatedEvent.builder()
                                                             .sourceAccount(sourceAccount)
                                                             .targetAccount(accountId)
                                                             .transferId(transferId)
                                                             .amount(usd(10.0))
                                                             .build()
        when:
            TransferEvent result = transferCreatedProcessor.process(event)
        then:
            result == TransferFailedEvent.of(transferId, reason)
        where:
            accountId           | transferId        || reason
            AccountId.of(1_000) | TransferId.of(4L) || "$accountId doesn't exist"
    }

    def "Should rethrow #exception from accountRegistry"() {
        given:
            TransferCreatedProcessor corruptedProcessor = new TransferCreatedProcessor(Stub(AccountRegistry) {
                findAccountById(accountId) >> { throw exception }
            })
        and:
            TransferCreatedEvent event = TransferCreatedEvent.builder()
                                                             .sourceAccount(accountId)
                                                             .targetAccount(AccountId.of(1L))
                                                             .transferId(TransferId.of(4L))
                                                             .amount(usd(10.0))
                                                             .build()
        when:
            corruptedProcessor.process(event)
        then:
            Exception thrownException = thrown()
        and:
            exception == thrownException
        where:
            accountId           | exception
            AccountId.of(1_000) | new NullPointerException()
    }
}