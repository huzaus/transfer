package com.shuzau.transfer.domain.transfer

import com.shuzau.transfer.domain.transaction.AccountId

import static com.shuzau.transfer.domain.core.Money.gbp

class DefaultTransferEventFactory {
    static TransferCompletedEvent defaultCompletedEvent(TransferId transferId) {
        TransferCompletedEvent.of(transferId)
    }
    static TransferFailedEvent defaultFailedEvent(TransferId transferId) {
        TransferFailedEvent.of(transferId, 'Ops')
    }

    static TransferCreatedEvent defaultCreatedEvent(TransferId transferId) {
        TransferCreatedEvent.builder()
                            .transferId(transferId)
                            .sourceAccount(AccountId.of(1L))
                            .targetAccount(AccountId.of(2L))
                            .amount(gbp(50.0))
                            .build()
    }
}
