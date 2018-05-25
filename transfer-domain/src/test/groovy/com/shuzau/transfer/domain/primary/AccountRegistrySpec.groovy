package com.shuzau.transfer.domain.primary

import com.shuzau.transfer.domain.core.Money
import com.shuzau.transfer.domain.transaction.Account
import com.shuzau.transfer.domain.transaction.AccountId
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

import static com.shuzau.transfer.domain.config.TransferDomainConfiguration.inMemoryAccountRegistry
import static java.util.Optional.empty

@Unroll
class AccountRegistrySpec extends Specification {
    @Shared
    AccountRegistry accountRegistry = inMemoryAccountRegistry()

    def "Should throw NullPointerException when balance is null on createAccountWithBalance"() {
        when:
            accountRegistry.createAccountWithBalance(null)
        then:
            thrown(NullPointerException)
    }

    def "Should create account with #amount balance on createAccountWithBalance"() {
        when:
            Account account = accountRegistry.createAccountWithBalance(amount)
        then:
            account.balance == amount
            account.id
        where:
            amount           | _
            Money.gbp(0.0)   | _
            Money.usd(10.0)  | _
            Money.pln(-10.0) | _
    }

    def "Should throw NullPointerException when accountId is null on findAccountById"() {
        when:
            accountRegistry.findAccountById(null)
        then:
            thrown(NullPointerException)
    }

    def "Should return empty optional if account doesn't exist"() {
        when:
            Optional<Account> account = accountRegistry.findAccountById(AccountId.of(1_000L))
        then:
            account == empty()
    }

    def "Should create account and find it by id"() {
        given:
            Account originalAccount = accountRegistry.createAccountWithBalance(Money.usd(100.0))
        when:
            Optional<Account> account = accountRegistry.findAccountById(originalAccount.id)
        then:
            account.get() == originalAccount
    }

    def "Should return empty optional on findAccountById for deleted account"() {
        given:
            Account account = accountRegistry.createAccountWithBalance(Money.usd(100.0))
        expect:
            account == accountRegistry.findAccountById(account.id).get()
        when:
            accountRegistry.deleteAccount(account.id)
        then:
            accountRegistry.findAccountById(account.id) == empty()
    }
}
