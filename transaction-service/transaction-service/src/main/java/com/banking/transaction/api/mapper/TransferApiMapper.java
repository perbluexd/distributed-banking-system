package com.banking.transaction.api.mapper;

import com.banking.transaction.api.dto.TransferResponse;
import com.banking.transaction.domain.model.Transfer;

public class TransferApiMapper {

    private TransferApiMapper() {
    }

    public static TransferResponse toResponse(Transfer transfer) {
        return new TransferResponse(
                transfer.getId().value(),
                transfer.getSourceAccountId().value(),
                transfer.getTargetAccountId().value(),
                transfer.getAmount().amount(),
                transfer.getAmount().currency(),
                transfer.getType(),
                transfer.getStatus(),
                transfer.getCreatedAt(),
                transfer.getUpdatedAt()
        );
    }
}