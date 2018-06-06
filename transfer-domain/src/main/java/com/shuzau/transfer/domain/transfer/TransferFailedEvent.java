package com.shuzau.transfer.domain.transfer;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Value;

@Value
@RequiredArgsConstructor(staticName = "of")
public class TransferFailedEvent implements TransferEvent {

    @NonNull
    private final TransferId transferId;
    @NonNull
    private final String reason;
}
