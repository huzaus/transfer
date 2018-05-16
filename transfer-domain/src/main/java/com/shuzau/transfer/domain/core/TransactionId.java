package com.shuzau.transfer.domain.core;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Value;

import static lombok.AccessLevel.PROTECTED;

@Value
@RequiredArgsConstructor(access = PROTECTED, staticName = "of")
public class TransactionId {

    @NonNull
    private final Long id;

}
