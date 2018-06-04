package com.shuzau.transfer.domain.transfer;

import com.shuzau.transfer.domain.core.Money;
import com.shuzau.transfer.domain.transaction.AccountId;
import lombok.Builder;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Value;

import static lombok.AccessLevel.PRIVATE;


@Value
@RequiredArgsConstructor(access = PRIVATE)
@Builder
public class TransferCreatedEvent implements TransferEvent {

    @NonNull
    private final TransferId transferId;
    @NonNull
    private final AccountId sourceAccount;
    @NonNull
    private final AccountId targetAccount;
    @NonNull
    private final Money amount;
}
