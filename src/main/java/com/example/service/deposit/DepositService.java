package com.example.service.deposit;

import com.example.model.Deposit;
import com.example.repository.IDepositRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Transactional
public class DepositService implements IDepositService {

    private IDepositRepository depositRepository;

    @Override
    public List<Deposit> findAll(boolean deleted) {
        return depositRepository
                .findAll()
                .stream()
                .filter(deposit -> deposit.getDeleted() == deleted)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Deposit> findById(Long aLong) {
        return Optional.empty();
    }

    @Override
    public void create(Deposit deposit) {
        deposit.setLocalDateTime(LocalDateTime.now());
        deposit.setDeleted(false);
        depositRepository.save(deposit);
    }

    @Override
    public void update(Long aLong, Deposit deposit) {

    }

    @Override
    public void removeById(Long aLong) {

    }


}
