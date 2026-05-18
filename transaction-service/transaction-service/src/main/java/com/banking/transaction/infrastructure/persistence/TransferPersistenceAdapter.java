package com.banking.transaction.infrastructure.persistence;

import com.banking.transaction.application.port.out.TransferRepositoryPort;
import com.banking.transaction.domain.model.AccountId;
import com.banking.transaction.domain.model.Transfer;
import com.banking.transaction.domain.model.TransferId;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class TransferPersistenceAdapter implements TransferRepositoryPort {

    private final TransferJpaRepository transferJpaRepository;

    public TransferPersistenceAdapter(TransferJpaRepository transferJpaRepository) {
        this.transferJpaRepository = transferJpaRepository;
    }

    @Override
    public Transfer save(Transfer transfer) {
        TransferJpaEntity entity = TransferPersistenceMapper.toEntity(transfer);
        TransferJpaEntity saved = transferJpaRepository.save(entity);
        return TransferPersistenceMapper.toDomain(saved);
    }

    @Override
    public Optional<Transfer> findById(TransferId transferId) {
        return transferJpaRepository.findById(transferId.value())
                .map(TransferPersistenceMapper::toDomain);
    }

    @Override
    public List<Transfer> findByAccountId(AccountId accountId) {
        return transferJpaRepository
                .findBySourceAccountIdOrTargetAccountId(accountId.value(), accountId.value())
                .stream()
                .map(TransferPersistenceMapper::toDomain)
                .toList();
    }
}