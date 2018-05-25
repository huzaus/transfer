package com.shuzau.transfer.domain.config

import com.shuzau.transfer.domain.core.Money
import com.shuzau.transfer.domain.exception.TransferException
import com.shuzau.transfer.domain.secondary.TransactionRepository
import com.shuzau.transfer.domain.transaction.AccountId
import com.shuzau.transfer.domain.transaction.Transaction
import com.shuzau.transfer.domain.transaction.TransactionId
import spock.lang.Shared
import spock.lang.Specification

import static java.util.Optional.empty

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
            Transaction transaction = transactionRepository.save(Transaction.createNewAccountTransaction(accountId, Money.usd(10.0))
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
            Transaction depositTransaction = initialTransaction.nextDepositTransaction(Money.gbp(100.0))
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
            Transaction outdatedTransaction = initialTransaction.nextWithdrawTransaction(Money.pln(100.0))
                                                                .withId(transactionRepository.nextTransactionId())
        and:
            transactionRepository.save(initialTransaction.nextWithdrawTransaction(Money.pln(55.0))
                                                         .withId(transactionRepository.nextTransactionId()))

        when:
            transactionRepository.save(outdatedTransaction)
        then:
            thrown(TransferException)
    }

    def "Should save deposit transaction"() {
        given:
            AccountId newAccountId = transactionRepository.newAccountId()
        and:
            Transaction initialTransaction = transactionRepository.save(Transaction.createNewAccountTransaction(newAccountId, Money.usd(100.0))
                                                                                   .withId(transactionRepository.nextTransactionId()))
        and:
            Transaction depositTransaction = initialTransaction.nextDepositTransaction(Money.usd(11.0))
                                                               .withId(transactionRepository.nextTransactionId())
        expect:
            initialTransaction == transactionRepository.getLatestTransactionByAccountId(newAccountId).get()
        when:
            transactionRepository.save(depositTransaction)
        and:
            Transaction transaction = transactionRepository.getLatestTransactionByAccountId(newAccountId).get()
        then:
            transaction == depositTransaction
    }

    def "Should save withdraw transaction"() {
        given:
            AccountId newAccountId = transactionRepository.newAccountId()
        and:
            Transaction initialTransaction = Transaction.createNewAccountTransaction(newAccountId, Money.usd(100.0))
                                                        .withId(transactionRepository.nextTransactionId())
            transactionRepository.save(initialTransaction)
        and:
            Transaction withdrawTransaction = initialTransaction.nextWithdrawTransaction(Money.usd(15.0))
                                                                .withId(transactionRepository.nextTransactionId())
        expect:
            initialTransaction == transactionRepository.getLatestTransactionByAccountId(newAccountId).get()
        when:
            transactionRepository.save(withdrawTransaction)
        then:
            withdrawTransaction == transactionRepository.getLatestTransactionByAccountId(newAccountId).get()
    }

    def "Should return empty transaction for deleted account on getLatestTransactionByAccountId"() {
        given:
            AccountId deletedAccount = transactionRepository.newAccountId()
        and:
            Transaction initialTransaction = transactionRepository.save(Transaction.createNewAccountTransaction(deletedAccount, Money.usd(100.0))
                                                                                   .withId(transactionRepository.nextTransactionId()))
        expect:
            initialTransaction == transactionRepository.getLatestTransactionByAccountId(deletedAccount).get()
        when:
            transactionRepository.delete(deletedAccount)
        then:
            empty() == transactionRepository.getLatestTransactionByAccountId(deletedAccount)
    }
}
