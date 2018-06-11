package com.shuzau.transfer.domain.transfer;

import com.shuzau.transfer.domain.exception.TransferException;

public enum TransferStatus {
    CREATED,
    COMPLETED,
    FAILED;

    public void assertCreated() {
        if (this != CREATED) {
            throw new TransferException(this + " cannot be changed");
        }
    }
}
