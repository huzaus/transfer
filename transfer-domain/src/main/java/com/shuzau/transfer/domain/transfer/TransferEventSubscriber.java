package com.shuzau.transfer.domain.transfer;

public interface TransferEventSubscriber<T> {

    void onEvent(T event);
}
