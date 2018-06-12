package com.shuzau.transfer.domain.config;

import com.shuzau.transfer.domain.primary.AccountRegistry;
import com.shuzau.transfer.domain.primary.TransferRegistry;
import com.shuzau.transfer.domain.secondary.TransactionRepository;
import com.shuzau.transfer.domain.secondary.TransferEventLog;
import com.shuzau.transfer.domain.secondary.TransferEventPublisher;
import com.shuzau.transfer.domain.transfer.TransferEvent;
import lombok.NonNull;

public class TransferDomainConfiguration {

    private TransferDomainConfiguration() {
    }

    public static AccountRegistry accountRegistry(@NonNull TransactionRepository transactionRepository) {
        return new PersistentAccountRegistry(transactionRepository);
    }

    public static TransferRegistry transferRegistry(TransferEventLog eventLog, TransferEventPublisher publisher) {
        publisher.subscribe(TransferEvent.class, eventLog::store);
        return new EventBasedTransferRegistry(eventLog, publisher);
    }


    static AccountRegistry inMemoryAccountRegistry() {
        return accountRegistry(new InMemoryTransactionRepository());
    }

    static TransferRegistry inMemoryTransferRegistry() {
        return transferRegistry(new InMemoryTransferEventLog(), new LocalTransferEventPublisher());
    }
}
