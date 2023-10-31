package com.example.service.withdraw;

import com.example.model.Withdraw;
import com.example.repository.IWithdrawRepository;
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
public class WithdrawService implements IWithdrawService {
    private IWithdrawRepository withdrawRepository;
    @Override
    public List<Withdraw> findAll(boolean deleted) {
        return withdrawRepository
                .findAll()
                .stream()
                .filter(withdraw -> withdraw.getDeleted()==deleted)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Withdraw> findById(Long aLong) {
        return Optional.empty();
    }

    @Override
    public void create(Withdraw withdraw) {
        withdraw.setDeleted(false);
        withdraw.setLocalDateTime(LocalDateTime.now());
        withdrawRepository.save(withdraw);
    }

    @Override
    public void update(Long aLong, Withdraw withdraw) {

    }

    @Override
    public void removeById(Long aLong) {

    }
}
