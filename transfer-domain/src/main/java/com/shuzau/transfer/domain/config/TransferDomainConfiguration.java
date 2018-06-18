package com.shuzau.transfer.domain.config;

import com.shuzau.transfer.domain.primary.TransferFacade;
import com.shuzau.transfer.domain.secondary.TransactionRepository;
import com.shuzau.transfer.domain.secondary.TransferEventLog;
import com.shuzau.transfer.domain.secondary.TransferEventBus;
import com.shuzau.transfer.domain.transfer.TransferCreatedEvent;
import com.shuzau.transfer.domain.transfer.TransferEvent;
import lombok.NonNull;

public class TransferDomainConfiguration {

    private TransferDomainConfiguration() {
    }

    public static TransferFacade transferFacade(TransferEventLog eventLog, TransferEventBus publisher, @NonNull TransactionRepository repository) {
        TransferDomainFacade transferDomainFacade = TransferDomainFacade.of(eventLog, publisher, repository);
        ExecuteTransferCommand executeTransferCommand = new ExecuteTransferCommand(transferDomainFacade, publisher);
        publisher.observe(TransferEvent.class)
                 .subscribe(eventLog::store);
        publisher.observe(TransferCreatedEvent.class)
                 .subscribe(event -> executeTransferCommand
                     .executeTransfer(event.getSourceAccount(), event.getTargetAccount(), event.getTransferId(), event.getAmount()));
        return transferDomainFacade;
    }

    public static TransferFacade inMemoryTransferFacade() {
        return transferFacade(new InMemoryTransferEventLog(), new LocalTransferEventBus(), new InMemoryTransactionRepository());
    }
}
