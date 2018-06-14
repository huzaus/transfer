package com.shuzau.transfer.domain.primary;

import java.util.Optional;

import com.shuzau.transfer.domain.core.Money;
import com.shuzau.transfer.domain.transaction.AccountId;
import com.shuzau.transfer.domain.transfer.Transfer;
import com.shuzau.transfer.domain.transfer.TransferId;

public interface TransferRegistry {

    Optional<Transfer> findTransferById(TransferId id);

    TransferId submitTransfer(AccountId sourceAccount, AccountId targetAccount, Money amount);
}
