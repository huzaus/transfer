package com.shuzau.transfer.domain.config;

import com.shuzau.transfer.domain.secondary.TransferEventBus;
import com.shuzau.transfer.domain.transfer.TransferEvent;
import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;
import lombok.NonNull;

class LocalTransferEventBus implements TransferEventBus {

    private Subject<TransferEvent> localBus = PublishSubject.create();

    @Override
    public void publish(@NonNull TransferEvent event) {
        if (localBus.hasObservers()) {
            localBus.onNext(event);
        }
    }

    @Override
    public <T extends TransferEvent> Observable<T> observe(@NonNull Class<T> type) {
        return localBus.filter(type::isInstance)
                       .cast(type);
    }
}
