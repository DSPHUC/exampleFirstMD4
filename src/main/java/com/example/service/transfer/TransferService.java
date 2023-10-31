package com.example.service.transfer;

import com.example.model.Transfer;
import com.example.repository.ITransferRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@AllArgsConstructor
public class TransferService implements ITransferService {
    private ITransferRepository transferRepository;

    @Override
    public List<Transfer> findAll(boolean deleted) {

        return transferRepository
                .findAll()
                .stream()
                .filter(transfer -> transfer.getDeleted()==deleted)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Transfer> findById(Long id) {
        return transferRepository.findById(id);
    }

    @Override
    public void create(Transfer transfer) {
        transfer.setDateTransfer(LocalDateTime.now());
        transfer.setDeleted(false);
        transferRepository.save(transfer);
    }

    @Override
    public void update(Long id, Transfer transfer) {

    }

    @Override
    public void removeById(Long id) {

    }
}
