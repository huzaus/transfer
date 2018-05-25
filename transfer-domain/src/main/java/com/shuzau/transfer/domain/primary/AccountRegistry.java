package com.shuzau.transfer.domain.primary;

import java.util.Optional;

import com.shuzau.transfer.domain.transaction.Account;
import com.shuzau.transfer.domain.transaction.AccountId;
import com.shuzau.transfer.domain.core.Money;

public interface AccountRegistry {

    Optional<Account> findAccountById(AccountId id);

    Account createAccountWithBalance(Money balance);

    void deleteAccount(AccountId id);
}
