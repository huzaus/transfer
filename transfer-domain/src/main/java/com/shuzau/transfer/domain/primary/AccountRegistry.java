package com.shuzau.transfer.domain.primary;

import java.util.Optional;

import com.shuzau.transfer.domain.entities.Account;
import com.shuzau.transfer.domain.entities.AccountId;
import com.shuzau.transfer.domain.entities.Money;

public interface AccountRegistry {

    Optional<Account> findAccountById(AccountId id);

    Account createAccountWithBalance(Money balance);

    void deleteAccount(AccountId id);
}
