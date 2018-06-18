package com.shuzau.transfer.domain.config;

import java.util.List;
import java.util.Optional;

import com.shuzau.transfer.domain.core.Money;
import com.shuzau.transfer.domain.primary.TransferFacade;
import com.shuzau.transfer.domain.secondary.TransactionRepository;
import com.shuzau.transfer.domain.secondary.TransferEventBus;
import com.shuzau.transfer.domain.secondary.TransferEventLog;
import com.shuzau.transfer.domain.transaction.Account;
import com.shuzau.transfer.domain.transaction.AccountId;
import com.shuzau.transfer.domain.transfer.Transfer;
import com.shuzau.transfer.domain.transfer.TransferCreatedEvent;
import com.shuzau.transfer.domain.transfer.TransferEvent;
import com.shuzau.transfer.domain.transfer.TransferId;
import lombok.NonNull;

class TransferDomainFacade implements TransferFacade {

    @NonNull
    private final TransferEventLog transferEventLog;
    @NonNull
    private final TransferEventBus transferEventBus;
    @NonNull
    private final TransactionRepository transactionRepository;

    private TransferDomainFacade(TransferEventLog transferEventLog, TransferEventBus transferEventBus,
        TransactionRepository transactionRepository) {
        this.transferEventLog = transferEventLog;
        this.transferEventBus = transferEventBus;
        this.transactionRepository = transactionRepository;
    }

    static TransferDomainFacade of(TransferEventLog eventLog, TransferEventBus publisher, TransactionRepository repository) {
        return new TransferDomainFacade(eventLog, publisher, repository);
    }

    @Override
    public Optional<Account> findAccountById(@NonNull AccountId id) {
        return transactionRepository.getLatestTransactionByAccountId(id)
                                    .map(transaction -> Account.from(transaction)
                                                               .withRepository(transactionRepository));
    }

    @Override
    public Account createAccountWithBalance(@NonNull Money balance) {
        return Account.newAccount(balance)
                      .withRepository(transactionRepository);
    }

    @Override
    public void deleteAccount(@NonNull AccountId id) {
        transactionRepository.delete(id);
    }

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
        transferEventBus.publish(TransferCreatedEvent.builder()
                                                     .transferId(transferId)
                                                     .sourceAccount(sourceAccount)
                                                     .targetAccount(targetAccount)
                                                     .amount(amount)
                                                     .build());
        return transferId;
    }
}
