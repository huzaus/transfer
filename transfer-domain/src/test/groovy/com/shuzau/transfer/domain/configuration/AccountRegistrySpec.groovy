package com.shuzau.transfer.domain.configuration

import com.shuzau.transfer.domain.entities.Account
import com.shuzau.transfer.domain.entities.AccountId
import com.shuzau.transfer.domain.primary.AccountRegistry
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

import static com.shuzau.transfer.domain.configuration.TransferDomainConfiguration.inMemoryAccountRegistry
import static com.shuzau.transfer.domain.entities.Money.gbp
import static com.shuzau.transfer.domain.entities.Money.pln
import static com.shuzau.transfer.domain.entities.Money.usd
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
            amount     | _
            gbp(0.0)   | _
            usd(10.0)  | _
            pln(-10.0) | _
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
            Account originalAccount = accountRegistry.createAccountWithBalance(usd(100.0))
        when:
            Optional<Account> account = accountRegistry.findAccountById(originalAccount.id)
        then:
            account.get() == originalAccount
    }

    def "Should return empty optional on findAccountById for deleted account" () {
        given:
            Account account = accountRegistry.createAccountWithBalance(usd(100.0))
        expect:
            account == accountRegistry.findAccountById(account.id).get()
        when:
            accountRegistry.deleteAccount(account.id)
        then:
            accountRegistry.findAccountById(account.id) == empty()
    }
}
