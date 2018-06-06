package com.shuzau.transfer.domain.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import com.shuzau.transfer.domain.secondary.TransferEventLog;
import com.shuzau.transfer.domain.transfer.TransferEvent;
import com.shuzau.transfer.domain.transfer.TransferId;
import lombok.NonNull;

public class InMemoryTransferEventLog implements TransferEventLog {

    private final AtomicLong sequence = new AtomicLong(0);
    private final Map<TransferId, List<TransferEvent>> storage = new ConcurrentHashMap<>();

    @Override
    public TransferId nextTransferId() {
        return TransferId.of(sequence.incrementAndGet());
    }

    @Override
    public void store(@NonNull TransferEvent event) {
        storage.compute(event.getTransferId(), (transferId, events) -> ensureNonNullAndAdd(events, event));
    }

    @Override
    public List<TransferEvent> findEvents(@NonNull TransferId transferId) {
        return new ArrayList<>(storage.computeIfAbsent(transferId, id -> new ArrayList<>()));
    }

    private List<TransferEvent> ensureNonNullAndAdd(List<TransferEvent> events, TransferEvent event) {
        events = Optional.ofNullable(events)
                         .orElseGet(ArrayList::new);
        events.add(event);
        return events;
    }
}
