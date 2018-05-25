package com.shuzau.transfer.domain.transaction

import com.shuzau.transfer.domain.config.InMemoryTransactionRepository
import com.shuzau.transfer.domain.exception.TransferException
import com.shuzau.transfer.domain.secondary.TransactionRepository
import com.shuzau.transfer.domain.transaction.Account
import com.shuzau.transfer.domain.transaction.AccountId
import com.shuzau.transfer.domain.transaction.Transaction
import spock.lang.Specification
import spock.lang.Unroll

import static com.shuzau.transfer.domain.core.Money.gbp
import static com.shuzau.transfer.domain.core.Money.usd

@Unroll
class AccountSpec extends Specification {

    private static TransactionRepository transactionRepository = new InMemoryTransactionRepository()

    private static Transaction sampleTransaction = Transaction.createNewAccountTransaction(transactionRepository.newAccountId(), usd(10.0))
                                                              .withId(transactionRepository.nextTransactionId())

    def "Should throw #exception when transaction = #transaction and transactionRepository = #repository on from"() {
        when:
            Account.from(transaction)
                   .withRepository(repository)
        then:
            thrown(exception)
        where:
            transaction       | repository            || exception
            null              | transactionRepository || NullPointerException
            sampleTransaction | null                  || NullPointerException
    }

    def "Should throw #exception when transaction = #transaction and balance = #balance on newAccount"() {
        when:
            Account.newAccount(balance)
                   .withRepository(repository)
        then:
            thrown(exception)
        where:
            balance   | repository            || exception
            null      | transactionRepository || NullPointerException
            usd(10.0) | null                  || NullPointerException
    }

    def "Should throw #exception when amount=#amount on withdraw"() {
        given:
            Account account = Account.newAccount(gbp(10.0))
                                     .withRepository(transactionRepository)
        when:
            account.withdraw(amount)
        then:
            thrown(exception)
        where:
            amount    || exception
            null      || NullPointerException
            gbp(-1.0) || TransferException
    }

    def "Should throw #exception when amount=#amount on deposit"() {
        given:
            Account account = Account.newAccount(gbp(10.0))
                                     .withRepository(transactionRepository)
        when:
            account.deposit(amount)
        then:
            thrown(exception)
        where:
            amount    || exception
            null      || NullPointerException
            gbp(-1.0) || TransferException
    }

    def "Should throw TransferException when balance(#balance) < amount(#amount) on withdraw"() {
        given:
            Account account = Account.newAccount(balance)
                                     .withRepository(transactionRepository)
        when:
            account.withdraw(amount)
        then:
            thrown(TransferException)
        where:
            balance   | amount
            usd(9.0)  | usd(10.0)
            usd(-1.0) | usd(1.0)
            usd(-1.0) | usd(0.0)
    }

    def "Account with #balance should have #expectedBalance after withdraw of #amount"() {
        given:
            Account account = Account.newAccount(balance)
                                     .withRepository(transactionRepository)
        when:
            account.withdraw(amount)
        then:
            account.balance == expectedBalance
        where:
            balance   | amount    || expectedBalance
            usd(12.0) | usd(10.0) || usd(2.0)
            usd(11.0) | usd(11.0) || usd(0.0)
            usd(5.0)  | usd(0.0)  || usd(5.0)
    }

    def "Account with #balance should have #expectedBalance after deposit of #amount"() {
        given:
            Account account = Account.newAccount(balance)
                                     .withRepository(transactionRepository)
        when:
            account.deposit(amount)
        then:
            account.balance == expectedBalance
        where:
            balance   | amount    || expectedBalance
            usd(12.0) | usd(10.0) || usd(22.0)
            usd(0.0)  | usd(11.0) || usd(11.0)
            usd(5.0)  | usd(0.0)  || usd(5.0)
            usd(-5.0) | usd(2.0)  || usd(-3.0)
    }


    def "Should create Account from transaction"() {
        given:
            AccountId accountId = transactionRepository.newAccountId()
            Transaction transaction = Transaction.createNewAccountTransaction(accountId, usd(10.0))
                                                 .withId(transactionRepository.nextTransactionId())
                                                 .nextDepositTransaction(usd(3.0))
                                                 .withId(transactionRepository.nextTransactionId())
                                                 .nextWithdrawTransaction(usd(4.5))
                                                 .withId(transactionRepository.nextTransactionId())
        when:
            Account account = Account.from(transaction)
                                     .withRepository(transactionRepository)
        then:
            with(account) {
                id == accountId
                balance == usd(8.5)
            }
    }
}
