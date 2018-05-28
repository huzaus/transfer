package com.shuzau.transfer.domain.transfer;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Value;

@Value
@RequiredArgsConstructor(staticName = "of")
public class TransferId {

    @NonNull
    private final Long id;

}
