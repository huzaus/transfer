package com.shuzau.transfer.domain.transfer;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Value;

import static lombok.AccessLevel.PRIVATE;

@Value
@RequiredArgsConstructor(access = PRIVATE, staticName = "of")
public class TransferCompletedEvent implements TransferEvent {

    @NonNull
    private final TransferId transferId;
}
