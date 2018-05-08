package com.shuzau.transfer.domain.primary;

import java.util.Optional;

import com.shuzau.transfer.domain.core.Account;
import com.shuzau.transfer.domain.core.AccountId;
import com.shuzau.transfer.domain.core.Money;

public interface AccountRegistry {

    Optional<Account> findAccountById(AccountId id);

    Account createAccountWithBalance(Money balance);

    void deleteAccount(AccountId id);
}
