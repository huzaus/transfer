package com.shuzau.transfer.domain.secondary;

import com.shuzau.transfer.domain.transfer.TransferEvent;
import com.shuzau.transfer.domain.transfer.TransferEventSubscriber;

public interface TransferEventPublisher {

    void subscribe(Class<? extends TransferEvent> clazz, TransferEventSubscriber<? super TransferEvent> subscriber);

    void publish(TransferEvent event);
}
