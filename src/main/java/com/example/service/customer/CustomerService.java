package com.example.service.customer;

import com.example.model.Customer;
import com.example.model.Deposit;
import com.example.model.Transfer;
import com.example.model.Withdraw;
import com.example.repository.ICustomerRepository;
import com.example.repository.IDepositRepository;
import com.example.repository.ITransferRepository;
import com.example.repository.IWithdrawRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@AllArgsConstructor
public class CustomerService implements ICustomerService {
    private ICustomerRepository customerRepository;

    private IDepositRepository depositRepository;

    private IWithdrawRepository withdrawRepository;

    private ITransferRepository transferRepository;

    @Override
    public List<Customer> findAll(boolean deleted) {
        return customerRepository.findAll()
                .stream().filter(customer -> customer.getDeleted() == deleted).collect(Collectors.toList());
//        return customers.stream().filter(c -> !c.getDeleted()).collect(Collectors.toList());
    }

    @Override
    public Optional<Customer> findById(Long id) {
        return customerRepository.findById(id).filter(customer -> !customer.getDeleted());
    }

    @Override
    public List<Customer> findAllWithoutId(Long id) {
        return customerRepository.findAll()
                .stream()
                .filter(customer -> !Objects.equals(customer.getId(), id) && !customer.getDeleted())
                .collect(Collectors.toList());
//        return customers.stream().filter(customer -> !Objects.equals(customer.getId(), id)).collect(Collectors.toList());
    }

    @Override
    public void create(Customer customer) {
        customer.setBalance(BigDecimal.ZERO);
        customer.setDeleted(false);
        customerRepository.save(customer);
    }

    @Override
    public void update(Long id, Customer customer) {
        Optional<Customer> customerOptional = customerRepository.findById(id);
        Customer customer1 = customerOptional.get();

        customer1.setFullName(customer.getFullName());
        customer1.setEmail(customer.getEmail());
        customer1.setPhone(customer.getPhone());
        customer1.setAddress(customer.getAddress());

        customerRepository.save(customer1);

//        int index = customers.indexOf(findById(id));
//        customers.set(index, customer);

    }

    @Override
    public void removeById(Long id) {
        customerRepository.findById(id).ifPresent(customer -> customer.setDeleted(true));
    }

    @Override
    public void restore(Long id) {
        customerRepository.findById(id).ifPresent(customer -> customer.setDeleted(false));
    }

    @Override
    public void deposit(Deposit deposit) {
        Customer customer = deposit.getCustomer();
        BigDecimal currentBalance = customer.getBalance();
        BigDecimal transactionAmount = deposit.getTransactionAmount();
        BigDecimal newBalance = currentBalance.add(transactionAmount);
        customer.setBalance(newBalance);

        update(customer.getId(), customer);
        deposit.setLocalDateTime(LocalDateTime.now());
        deposit.setDeleted(false);
        depositRepository.save(deposit);

    }

    @Override
    public void withdraw(Withdraw withdraw) {
        Customer customer = withdraw.getCustomer();
        BigDecimal currentBalance = customer.getBalance();
        BigDecimal transactionAmount = withdraw.getTransactionAmount();
        BigDecimal newBalance = currentBalance.subtract(transactionAmount);
        customer.setBalance(newBalance);

        update(customer.getId(), customer);
        withdrawRepository.save(withdraw);
    }

    @Override
    public void transfer(Transfer transfer) {
        Customer sender = transfer.getSender();
        BigDecimal senderBalance = sender.getBalance();

        Customer recipient = transfer.getRecipient();
        BigDecimal recipientBalance = recipient.getBalance();

        Long fees = 10L;
        transfer.setFees(fees);

        BigDecimal transferAmount = transfer.getTransferAmount();
        BigDecimal feesAmount = transferAmount.multiply(BigDecimal.valueOf(fees)).divide(BigDecimal.valueOf(100));
        transfer.setFeesAmount(feesAmount);

        BigDecimal transactionAmount = transferAmount.add(feesAmount);
        transfer.setTransactionAmount(transactionAmount);

        BigDecimal newSenderBalance = senderBalance.subtract(transactionAmount);
        sender.setBalance(newSenderBalance);
        BigDecimal newRecipientBalance = recipientBalance.add(transferAmount);
        recipient.setBalance(newRecipientBalance);

        customerRepository.save(sender);
        customerRepository.save(recipient);

        transferRepository.save(transfer);
    }

}
