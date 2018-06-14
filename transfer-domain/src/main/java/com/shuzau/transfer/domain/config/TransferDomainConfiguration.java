package com.shuzau.transfer.domain.config;

import com.shuzau.transfer.domain.primary.TransferFacade;
import com.shuzau.transfer.domain.secondary.TransactionRepository;
import com.shuzau.transfer.domain.secondary.TransferEventLog;
import com.shuzau.transfer.domain.secondary.TransferEventPublisher;
import lombok.NonNull;

public class TransferDomainConfiguration {

    private TransferDomainConfiguration() {
    }

    public static TransferFacade transferFacade(TransferEventLog eventLog, TransferEventPublisher publisher, @NonNull TransactionRepository repository) {
        return TransferDomainFacade.of(eventLog, publisher, repository);
    }

    public static TransferFacade inMemoryTransferFacade() {
        return transferFacade(new InMemoryTransferEventLog(), new LocalTransferEventPublisher(), new InMemoryTransactionRepository());
    }
}
