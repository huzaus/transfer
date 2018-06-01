package com.shuzau.transfer.domain.transfer;

import com.shuzau.transfer.domain.core.Money;
import com.shuzau.transfer.domain.transaction.AccountId;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import static com.shuzau.transfer.domain.transfer.TransferState.COMPLETED;
import static com.shuzau.transfer.domain.transfer.TransferState.CREATED;
import static com.shuzau.transfer.domain.transfer.TransferState.FAILED;
import static lombok.AccessLevel.PRIVATE;

@RequiredArgsConstructor(access = PRIVATE)
@ToString
@EqualsAndHashCode
@Getter
public class Transfer {

    @NonNull
    private final TransferId transferId;
    @NonNull
    private final AccountId sourceAccount;
    @NonNull
    private final AccountId targetAccount;
    @NonNull
    private final Money amount;
    @NonNull
    private TransferState state = CREATED;

    public static Transfer create(@NonNull TransferCreatedEvent event) {
        return new Transfer(event.getTransferId(), event.getSourceAccount(), event.getTargetAccount(), event.getAmount());
    }

    public void apply(TransferCompletedEvent event) {
        state = COMPLETED;
    }

    public void apply(TransferFailedEvent event) {
        state = FAILED;
    }
}
