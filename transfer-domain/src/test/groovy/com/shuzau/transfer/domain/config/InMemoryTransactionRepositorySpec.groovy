package com.shuzau.transfer.domain.config

import com.shuzau.transfer.domain.core.Money
import com.shuzau.transfer.domain.exception.TransferException
import com.shuzau.transfer.domain.secondary.TransactionRepository
import com.shuzau.transfer.domain.transaction.AccountId
import com.shuzau.transfer.domain.transaction.Transaction
import com.shuzau.transfer.domain.transaction.TransactionId
import com.shuzau.transfer.domain.transfer.TransferId
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

import static com.shuzau.transfer.domain.core.Money.usd
import static java.util.Optional.empty

@Unroll
class InMemoryTransactionRepositorySpec extends Specification {
    @Shared
    TransactionRepository transactionRepository = new InMemoryTransactionRepository()

    def "Should incremented account id sequence on newAccountId"() {
        given:
            AccountId accountId = transactionRepository.newAccountId()
        when:
            AccountId newAccountId = transactionRepository.newAccountId()
        then:
            accountId.id + 1 == newAccountId.id
    }

    def "Should incremented transaction id sequence on nextTransactionId"() {
        given:
            TransactionId transactionId = transactionRepository.nextTransactionId()
        when:
            TransactionId nextTransactionId = transactionRepository.nextTransactionId()
        then:
            transactionId.id + 1 == nextTransactionId.id
    }

    def "Should throw NullPointerException then account id is null on getLatestTransactionByAccountId"() {
        when:
            transactionRepository.getLatestTransactionByAccountId(null)
        then:
            thrown(NullPointerException)
    }

    def "Should return an empty optional when account doesn't exist on getLatestTransactionByAccountId"() {
        given:
            Optional transaction = transactionRepository.getLatestTransactionByAccountId(AccountId.of(1_000))
        expect:
            transaction == empty()
    }

    def "Should return saved transaction for account id"() {
        given:
            AccountId accountId = transactionRepository.newAccountId()
        and:
            Transaction transaction = transactionRepository.save(Transaction.createNewAccountTransaction(accountId, usd(10.0))
                                                                            .withId(transactionRepository.nextTransactionId()))
        when:
            Optional optional = transactionRepository.getLatestTransactionByAccountId(accountId)
        then:
            optional.get() == transaction
    }


    def "Should throw TransferException on saving non initial transaction for newly created account"() {
        given:
            AccountId accountId = transactionRepository.newAccountId()
        and:
            Transaction initialTransaction = Transaction.createNewAccountTransaction(accountId, Money.gbp(100.0))
                                                        .withId(transactionRepository.nextTransactionId())
        and:
            Transaction depositTransaction = initialTransaction.nextTransaction(Money.gbp(100.0))
                                                               .withId(transactionRepository.nextTransactionId())
        when:
            transactionRepository.save(depositTransaction)
        then:
            thrown(TransferException)
    }

    def "Should throw TransferException on saving outdated transaction"() {
        given:
            AccountId accountId = transactionRepository.newAccountId()
        and:
            Transaction initialTransaction = transactionRepository.save(Transaction.createNewAccountTransaction(accountId, Money.pln(100.0))
                                                                                   .withId(transactionRepository.nextTransactionId()))
        and:
            Transaction outdatedTransaction = initialTransaction.nextTransaction(Money.pln(100.0))
                                                                .withId(transactionRepository.nextTransactionId())
        and:
            transactionRepository.save(initialTransaction.nextTransaction(Money.pln(55.0))
                                                         .withId(transactionRepository.nextTransactionId()))

        when:
            transactionRepository.save(outdatedTransaction)
        then:
            thrown(TransferException)
    }

    def "Should save with #amount transaction"() {
        given:
            AccountId newAccountId = transactionRepository.newAccountId()
        and:
            Transaction initialTransaction = transactionRepository.save(Transaction.createNewAccountTransaction(newAccountId, usd(100.0))
                                                                                   .withId(transactionRepository.nextTransactionId()))
        and:
            Transaction depositTransaction = initialTransaction.nextTransaction(amount)
                                                               .withId(transactionRepository.nextTransactionId())
        expect:
            initialTransaction == transactionRepository.getLatestTransactionByAccountId(newAccountId).get()
        when:
            transactionRepository.save(depositTransaction)
        and:
            Transaction transaction = transactionRepository.getLatestTransactionByAccountId(newAccountId).get()
        then:
            transaction == depositTransaction
        where:
            amount     | _
            usd(-10.0) | _
            usd(0.0)   | _
            usd(10.0)  | _
    }

    def "Should save with #transferId and # amount transfer transaction"() {
        given:
            AccountId newAccountId = transactionRepository.newAccountId()
        and:
            Transaction initialTransaction = transactionRepository.save(Transaction.createNewAccountTransaction(newAccountId, usd(100.0))
                                                                                   .withId(transactionRepository.nextTransactionId()))
        and:
            Transaction depositTransaction = initialTransaction.nextTransferTransaction(transferId, amount)
                                                               .withId(transactionRepository.nextTransactionId())
        expect:
            initialTransaction == transactionRepository.getLatestTransactionByAccountId(newAccountId).get()
        when:
            transactionRepository.save(depositTransaction)
        and:
            Transaction transaction = transactionRepository.getLatestTransactionByAccountId(newAccountId).get()
        then:
            transaction == depositTransaction
        where:
            transferId           | amount
            TransferId.of(1_000) | usd(-10.0)
            TransferId.of(1_001) | usd(0.0)
            TransferId.of(1_002) | usd(10.0)
    }

    def "Should return empty transaction for deleted account on getLatestTransactionByAccountId"() {
        given:
            AccountId deletedAccount = transactionRepository.newAccountId()
        and:
            Transaction initialTransaction = transactionRepository.save(Transaction.createNewAccountTransaction(deletedAccount, usd(100.0))
                                                                                   .withId(transactionRepository.nextTransactionId()))
        expect:
            initialTransaction == transactionRepository.getLatestTransactionByAccountId(deletedAccount).get()
        when:
            transactionRepository.delete(deletedAccount)
        then:
            empty() == transactionRepository.getLatestTransactionByAccountId(deletedAccount)
    }
}
