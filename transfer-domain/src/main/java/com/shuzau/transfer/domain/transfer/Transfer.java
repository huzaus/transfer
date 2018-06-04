package com.shuzau.transfer.domain.transfer;

import com.shuzau.transfer.domain.core.Money;
import com.shuzau.transfer.domain.exception.TransferException;
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

    public Transfer apply(@NonNull TransferCompletedEvent event) {
        validate(event);
        state = COMPLETED;
        return this;
    }

    public Transfer apply(@NonNull TransferFailedEvent event) {
        validate(event);
        state = FAILED;
        return this;
    }

    private void validate(TransferEvent event) {
        if (!transferId.equals(event.getTransferId())) {
            throw new TransferException(event + " has different transfer Id, expected :" + transferId);
        }
        if (state != CREATED) {
            throw new TransferException(state + " cannot be changed");
        }

    }
}
