package com.shuzau.transfer.domain.entities;

import lombok.RequiredArgsConstructor;
import lombok.Value;

import static lombok.AccessLevel.PRIVATE;

@Value
@RequiredArgsConstructor(access = PRIVATE)
public class TransactionId {

    private static final TransactionId INITIAL_ID = new TransactionId(0L);

    private final Long id;

    static TransactionId initial() {
        return INITIAL_ID;
    }

    boolean isInitial() {
        return 0L == id;
    }

    TransactionId nextId() {
        return new TransactionId(id + 1);
    }
}
