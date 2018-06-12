package com.shuzau.transfer.domain.config;

import java.util.List;
import java.util.Optional;

import com.shuzau.transfer.domain.core.Money;
import com.shuzau.transfer.domain.primary.TransferRegistry;
import com.shuzau.transfer.domain.secondary.TransferEventLog;
import com.shuzau.transfer.domain.secondary.TransferEventPublisher;
import com.shuzau.transfer.domain.transaction.AccountId;
import com.shuzau.transfer.domain.transfer.Transfer;
import com.shuzau.transfer.domain.transfer.TransferCreatedEvent;
import com.shuzau.transfer.domain.transfer.TransferEvent;
import com.shuzau.transfer.domain.transfer.TransferId;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
class EventBasedTransferRegistry implements TransferRegistry {

    @NonNull
    private final TransferEventLog transferEventLog;
    @NonNull
    private final TransferEventPublisher transferEventPublisher;


    @Override
    public Optional<Transfer> findTransferById(@NonNull TransferId id) {
        List<TransferEvent> transferEvents = transferEventLog.findEvents(id);
        return Optional.of(transferEvents)
                       .filter(events -> !events.isEmpty())
                       .map(Transfer::build);
    }

    @Override
    public TransferId submitTransfer(@NonNull AccountId sourceAccount, @NonNull AccountId targetAccount, @NonNull Money amount) {
        TransferId transferId = transferEventLog.nextTransferId();
        transferEventPublisher.publish(TransferCreatedEvent.builder()
                                                           .transferId(transferId)
                                                           .sourceAccount(sourceAccount)
                                                           .targetAccount(targetAccount)
                                                           .amount(amount)
                                                           .build());
        return transferId;
    }
}
