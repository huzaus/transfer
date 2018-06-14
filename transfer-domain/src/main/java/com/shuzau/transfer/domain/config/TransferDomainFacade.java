package com.shuzau.transfer.domain.config;

import java.util.List;
import java.util.Optional;

import com.shuzau.transfer.domain.core.Money;
import com.shuzau.transfer.domain.primary.TransferFacade;
import com.shuzau.transfer.domain.secondary.TransactionRepository;
import com.shuzau.transfer.domain.secondary.TransferEventLog;
import com.shuzau.transfer.domain.secondary.TransferEventPublisher;
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
    private final TransferEventPublisher transferEventPublisher;
    @NonNull
    private final TransactionRepository transactionRepository;

    private TransferDomainFacade(TransferEventLog transferEventLog, TransferEventPublisher transferEventPublisher,
        TransactionRepository transactionRepository) {
        this.transferEventLog = transferEventLog;
        this.transferEventPublisher = transferEventPublisher;
        this.transactionRepository = transactionRepository;
    }

    public static TransferDomainFacade of(TransferEventLog eventLog, TransferEventPublisher publisher, TransactionRepository repository) {
        publisher.subscribe(TransferEvent.class, eventLog::store);
        return new TransferDomainFacade(eventLog, publisher, repository);
    }

    @Override
    public Optional<Account> findAccountById(AccountId id) {
        return transactionRepository.getLatestTransactionByAccountId(id)
                                    .map(transaction -> Account.from(transaction)
                                                               .withRepository(transactionRepository));
    }

    @Override
    public Account createAccountWithBalance(Money balance) {
        return Account.newAccount(balance)
                      .withRepository(transactionRepository);
    }

    @Override
    public void deleteAccount(AccountId id) {
        transactionRepository.delete(id);
    }

    @Override
    public Optional<Transfer> findTransferById(TransferId id) {
        List<TransferEvent> transferEvents = transferEventLog.findEvents(id);
        return Optional.of(transferEvents)
                       .filter(events -> !events.isEmpty())
                       .map(Transfer::build);
    }

    @Override
    public TransferId submitTransfer(AccountId sourceAccount, AccountId targetAccount, Money amount) {
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
