package com.shuzau.transfer.domain.config;

import com.shuzau.transfer.domain.primary.AccountRegistry;

public class TransferDomainConfiguration {

    private TransferDomainConfiguration() {
    }

    public static AccountRegistry inMemoryAccountRegistry() {
        return new PersistentAccountRegistry(new InMemoryTransactionRepository());
    }
}
