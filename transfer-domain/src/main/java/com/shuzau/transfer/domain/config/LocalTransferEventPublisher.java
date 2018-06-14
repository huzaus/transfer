package com.shuzau.transfer.domain.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import com.shuzau.transfer.domain.secondary.TransferEventPublisher;
import com.shuzau.transfer.domain.transfer.TransferEvent;
import com.shuzau.transfer.domain.transfer.TransferEventSubscriber;
import io.vavr.control.Try;
import lombok.NonNull;

class LocalTransferEventPublisher implements TransferEventPublisher {

    private Map<Class<? extends TransferEvent>, List<TransferEventSubscriber<? super TransferEvent>>> subscribers = new ConcurrentHashMap<>();

    @Override
    public void subscribe(@NonNull Class<? extends TransferEvent> clazz, @NonNull TransferEventSubscriber<? super TransferEvent> subscriber) {
        subscribers.compute(clazz,
            (aClass, subscribers) -> ensureNonNullAndAdd(subscribers, subscriber));
    }

    @Override
    public void publish(@NonNull TransferEvent event) {
        subscribers.keySet()
                   .stream()
                   .filter(clazz -> clazz.isInstance(event))
                   .map(subscribers::get)
                   .flatMap(List::stream)
                   .forEach(subscriber -> Try.run(() -> subscriber.onEvent(event)));

    }

    private List<TransferEventSubscriber<? super TransferEvent>> ensureNonNullAndAdd(
        List<TransferEventSubscriber<? super TransferEvent>> subscribers,
        TransferEventSubscriber<? super TransferEvent> subscriber) {

        subscribers = Optional.ofNullable(subscribers)
                              .orElseGet(ArrayList::new);
        subscribers.add(subscriber);
        return subscribers;
    }
}
