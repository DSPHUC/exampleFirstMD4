package com.example.service.customer;

import com.example.model.Customer;
import com.example.model.Deposit;
import com.example.model.Transfer;
import com.example.model.Withdraw;
import com.example.service.IGeneralService;

import java.util.List;
import java.util.Optional;

public interface ICustomerService extends IGeneralService<Customer, Long> {
    void deposit(Deposit deposit);

    List<Customer> findAllWithoutId(Long id);

    void withdraw(Withdraw withdraw);

    void transfer(Transfer transfer);

    void restore(Long id);

    Deposit findDepositByCustomerId(Long customerId);

    Withdraw findWithdrawByCustomerId(Long customerId);
}
