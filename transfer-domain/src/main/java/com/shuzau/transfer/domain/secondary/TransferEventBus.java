package com.shuzau.transfer.domain.secondary;

import com.shuzau.transfer.domain.transfer.TransferEvent;
import io.reactivex.Observable;

public interface TransferEventBus {

    <T extends TransferEvent> Observable<T> observe(Class<T> clazz);

    <T extends TransferEvent> void publish(T event);
}
