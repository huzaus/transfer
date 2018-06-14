package com.shuzau.transfer.domain.transfer;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import com.shuzau.transfer.domain.core.Money;
import com.shuzau.transfer.domain.exception.TransferException;
import com.shuzau.transfer.domain.transaction.AccountId;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.ToString;

import static com.shuzau.transfer.domain.transfer.TransferStatus.COMPLETED;
import static com.shuzau.transfer.domain.transfer.TransferStatus.CREATED;
import static com.shuzau.transfer.domain.transfer.TransferStatus.FAILED;
import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;
import static io.vavr.Predicates.instanceOf;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
@ToString
@EqualsAndHashCode
@Getter
public class Transfer {

    private TransferId transferId;
    private AccountId sourceAccount;
    private AccountId targetAccount;
    private Money amount;
    private TransferStatus status;

    public static Transfer create(@NonNull TransferCreatedEvent event) {
        return new Transfer().apply(event);
    }

    public static Transfer build(@NonNull List<TransferEvent> events) {
        if (events.isEmpty()) {
            throw new TransferException("Events list is empty: " + events);
        }
        Transfer transfer = new Transfer();
        events.forEach(transfer::apply);
        return transfer;
    }

    private Transfer apply(@NonNull TransferEvent transferEvent) {
        return Match(transferEvent).of(
            Case($(instanceOf(TransferCreatedEvent.class)), this::apply),
            Case($(instanceOf(TransferCompletedEvent.class)), this::apply),
            Case($(instanceOf(TransferFailedEvent.class)), this::apply),
            Case($(), event -> {
                throw new TransferException("Unsupported event type: " + event);
            }));
    }

    private Transfer apply(@NonNull TransferCreatedEvent event) {
        if (Objects.nonNull(transferId)) {
            throw new TransferException("Transfer is already initialized: " + this);
        }
        transferId = event.getTransferId();
        sourceAccount = event.getSourceAccount();
        targetAccount = event.getTargetAccount();
        amount = event.getAmount();
        status = CREATED;
        return this;
    }

    private Transfer apply(@NonNull TransferCompletedEvent event) {
        assertStatus();
        assertTransferIdIsTheSame(event);
        status.assertCreated();
        status = COMPLETED;
        return this;
    }

    private Transfer apply(@NonNull TransferFailedEvent event) {
        assertStatus();
        assertTransferIdIsTheSame(event);
        status = FAILED;
        return this;
    }

    private void assertTransferIdIsTheSame(TransferEvent event) {
        if (!transferId.equals(event.getTransferId())) {
            throw new TransferException(event + " has different transfer Id, expected :" + transferId);
        }
    }

    private void assertStatus() {
        Optional.ofNullable(status)
                .orElseThrow(() -> new TransferException("Transfer wasn't initialized with TransferCreatedEvent."))
                .assertCreated();
    }
}
