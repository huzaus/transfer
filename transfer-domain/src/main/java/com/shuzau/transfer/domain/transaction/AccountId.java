package com.shuzau.transfer.domain.transaction;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Value;

@Value
@RequiredArgsConstructor(staticName = "of")
public class AccountId {

    @NonNull
    private final Long id;
}
