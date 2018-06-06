package com.shuzau.transfer.domain.secondary;

import java.util.List;

import com.shuzau.transfer.domain.transfer.TransferEvent;
import com.shuzau.transfer.domain.transfer.TransferId;

public interface TransferEventLog {

    TransferId nextTransferId();

    void store(TransferEvent event);

    List<TransferEvent> findEvents(TransferId transferId);
}
