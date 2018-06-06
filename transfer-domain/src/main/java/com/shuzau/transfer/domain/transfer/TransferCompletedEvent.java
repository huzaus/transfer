package com.shuzau.transfer.domain.transfer;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Value;

@Value
@RequiredArgsConstructor(staticName = "of")
public class TransferCompletedEvent implements TransferEvent {

    @NonNull
    private final TransferId transferId;
}
