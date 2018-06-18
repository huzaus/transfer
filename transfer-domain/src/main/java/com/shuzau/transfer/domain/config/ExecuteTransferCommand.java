package com.shuzau.transfer.domain.config;

import com.shuzau.transfer.domain.core.Money;
import com.shuzau.transfer.domain.exception.TransferException;
import com.shuzau.transfer.domain.primary.AccountRegistry;
import com.shuzau.transfer.domain.secondary.TransferEventBus;
import com.shuzau.transfer.domain.transaction.Account;
import com.shuzau.transfer.domain.transaction.AccountId;
import com.shuzau.transfer.domain.transfer.TransferCompletedEvent;
import com.shuzau.transfer.domain.transfer.TransferEvent;
import com.shuzau.transfer.domain.transfer.TransferFailedEvent;
import com.shuzau.transfer.domain.transfer.TransferId;
import io.vavr.control.Try;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import static com.shuzau.transfer.domain.core.Validators.multiValidator;

@RequiredArgsConstructor
class ExecuteTransferCommand {

    private final AccountRegistry accountRegistry;
    private final TransferEventBus transferEventBus;

    public void executeTransfer(@NonNull AccountId sourceAccountId, @NonNull AccountId targetAccountId, @NonNull TransferId transferId,
        @NonNull Money amount) {
        transferEventBus.publish(Try.of(() -> processTransfer(sourceAccountId, targetAccountId, transferId, amount))
                                    .recover(TransferException.class, exception -> TransferFailedEvent.of(transferId, exception.getMessage()))
                                    .get());
    }

    private TransferEvent processTransfer(@NonNull AccountId sourceAccountId, @NonNull AccountId targetAccountId, @NonNull TransferId transferId,
        @NonNull Money amount) {
        Account sourceAccount = accountRegistry.findAccountById(sourceAccountId)
                                               .orElseThrow(() -> new TransferException(sourceAccountId + " doesn't exist"));
        multiValidator().accept(sourceAccount.validateTransfer(amount.negate()));

        Account targetAccount = accountRegistry.findAccountById(targetAccountId)
                                               .orElseThrow(() -> new TransferException(targetAccountId + " doesn't exist"));
        multiValidator().accept(targetAccount.validateTransfer(amount));

        sourceAccount.transfer(transferId, amount.negate());
        targetAccount.transfer(transferId, amount);
        return TransferCompletedEvent.of(transferId);
    }
}
