package com.shuzau.transfer.domain.entities

import com.shuzau.transfer.domain.exception.TransferException
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

import static com.shuzau.transfer.domain.entities.Money.usd

@Unroll
class AccountSpec extends Specification {

    @Shared
    private AccountId accountId = AccountId.of(1L)

    def "Should throw #exception when id = #id and money = #money"() {
        when:
            Account.builder()
                   .id(AccountId.of(id))
                   .balance(money)
                   .build()
        then:
            thrown(exception)
        where:
            id   | money     || exception
            null | usd(10.0) || NullPointerException
            1L   | null      || NullPointerException
    }

    def "Should throw #exception when amount=#amount on withdraw"() {
        given:
            Account account = Account.builder()
                                     .id(accountId)
                                     .balance(usd(10.0))
                                     .build()
        when:
            account.withdraw(amount)
        then:
            thrown(exception)
        where:
            amount    || exception
            null      || NullPointerException
            usd(-1.0) || TransferException
    }

    def "Should throw #exception when amount=#amount on deposit"() {
        given:
            Account account = Account.builder()
                                     .id(accountId)
                                     .balance(usd(10.0))
                                     .build()
        when:
            account.deposit(amount)
        then:
            thrown(exception)
        where:
            amount    || exception
            null      || NullPointerException
            usd(-1.0) || TransferException
    }

    def "Should throw TransferException when balance(#balance) < amount(#amount) on withdraw"() {
        given:
            Account account = Account.builder()
                                     .id(accountId)
                                     .balance(balance)
                                     .build()
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
            Account account = Account.builder()
                                     .id(accountId)
                                     .balance(balance)
                                     .build()
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
            Account account = Account.builder()
                                     .id(accountId)
                                     .balance(balance)
                                     .build()
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

    def "Should throw NullPointerException when transaction is null on from"() {
        when:
            Account.from(null)
        then:
            thrown(NullPointerException)
    }

    def "Should create Account from transaction"() {
        given:
            Transaction transaction = Transaction.createNewAccountTransaction(accountId, usd(10.0))
                                                 .nextDepositTransaction(usd(3.0))
                                                 .nextWithdrawTransaction(usd(4.5))
        when:
            Account account = Account.from(transaction)
        then:
            with(account) {
                id == accountId
                balance == usd(8.5)
            }
    }
}
