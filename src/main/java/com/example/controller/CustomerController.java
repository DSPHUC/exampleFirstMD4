package com.example.controller;

import com.example.model.Customer;
import com.example.model.Deposit;
import com.example.model.Transfer;
import com.example.model.Withdraw;
import com.example.service.customer.ICustomerService;
import com.example.service.deposit.IDepositService;
import com.example.service.transfer.ITransferService;
import com.example.service.withdraw.IWithdrawService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/customers")
public class CustomerController {
    private final ICustomerService customerService;
    private final IDepositService depositService;
    private final IWithdrawService withdrawService;
    private final ITransferService transferService;

    public CustomerController(ICustomerService customerService, IDepositService depositService, IWithdrawService withdrawService, ITransferService transferService) {
        this.customerService = customerService;
        this.depositService = depositService;
        this.withdrawService = withdrawService;
        this.transferService = transferService;
    }

    @GetMapping
    public String listCustomer(Model model) {
//        List<Customer> customers = customerService.findAll();
        model.addAttribute("customers", customerService.findAll(false));
        return "customer/list";
    }

    @GetMapping("/listBan")
    public String listBan(Model model) {
        List<Customer> customers = customerService.findAll(true);
        model.addAttribute("customerBan", customers);
        return "customer/listBan";
    }

    @GetMapping("/create")
    public String showCreateCustomer(Model model) {
        model.addAttribute("customer", new Customer());
        return "customer/create";
    }

    @PostMapping("/create")
    public String createCustomer(@ModelAttribute Customer customer, Model model, BindingResult bindingResult) {
        new Customer().validate(customer, bindingResult);
        if (bindingResult.hasErrors()) {
            model.addAttribute("success", false);
            model.addAttribute("message", "Created unsuccessful");
            model.addAttribute("error", true);
            model.addAttribute("checkCustomer", true);
            model.addAttribute("customer", customer);
            return "customer/create";
        }
        customerService.create(customer);
        model.addAttribute("success", true);
        model.addAttribute("message", "Created successfully");
        model.addAttribute("customer", new Customer());
        return "customer/create";
    }

    @GetMapping("/update/{id}")
    public ModelAndView showUpdateCustomer(@PathVariable long id) {
        ModelAndView modelAndView = new ModelAndView("customer/edit");
        Optional<Customer> customerOptional = customerService.findById(id);
        Customer customer = customerOptional.get();
        modelAndView.addObject("customerUpdate", customer);
        return modelAndView;
    }

    @PostMapping("/update/{idCustomer}")
    public String updateCustomer(Model model, @PathVariable Long idCustomer, @ModelAttribute Customer customer, BindingResult bindingResult) {
        new Customer().validate(customer, bindingResult);
        if (bindingResult.hasErrors()) {
            model.addAttribute("success", false);
            model.addAttribute("message", "Updated unsuccessful");
            model.addAttribute("error", true);
            model.addAttribute("checkCustomer", true);
            model.addAttribute("customerUpdate", customer);
            return "customer/edit";
        }
        customerService.update(idCustomer, customer);
        model.addAttribute("message", "Updated successfully");
        model.addAttribute("success", true);
        model.addAttribute("customerUpdate", customer);
        return "customer/edit";
    }

    @GetMapping("/listBan/{id}")
    public String restore(@PathVariable long id, RedirectAttributes redirectAttributes) {
        customerService.restore(id);
        redirectAttributes.addFlashAttribute("success", true);
        redirectAttributes.addFlashAttribute("message", "Unban successfully");
        return "redirect:/customers/listBan";
    }

    @GetMapping("/delete/{idCustomer}")
    public String delete(@PathVariable long idCustomer, RedirectAttributes redirectAttributes) {
        customerService.removeById(idCustomer);
        redirectAttributes.addFlashAttribute("success", true);
        redirectAttributes.addFlashAttribute("message", "Ban successfully");
        return "redirect:/customers";
    }

    @GetMapping("/deposit/{id}")
    public String showDeposit(@PathVariable Long id, Model model) {
        Optional<Customer> customerOptional = customerService.findById(id);
        Customer customer = customerOptional.get();
        Deposit deposit = new Deposit();
        deposit.setCustomer(customer);
        model.addAttribute("deposit", deposit);
        return "customer/deposit";
    }

    @PostMapping("/deposit/{customerId}")
    public String deposit(Model model, @PathVariable Long customerId, @ModelAttribute Deposit deposit, BindingResult bindingResult) {
        new Deposit().validate(deposit, bindingResult);
        Optional<Customer> customerOptional = customerService.findById(customerId);
        Customer customer = customerOptional.get();

        deposit.setCustomer(customer);

        if (bindingResult.hasErrors()) {
            model.addAttribute("success", false);
            model.addAttribute("message", "Deposit unsuccessful");
            model.addAttribute("error", true);
            model.addAttribute("checkDeposit", true);
            model.addAttribute("deposit", deposit);
            return "customer/deposit";
        }
        customerService.deposit(deposit);

        deposit.setTransactionAmount(null);

        Customer customerNew = customerService.findById(customerId).get();
        deposit.setCustomer(customerNew);
        model.addAttribute("deposit", deposit);

        model.addAttribute("message", "Deposit successfully ");
        model.addAttribute("success", true);
        return "customer/deposit";
    }

    @GetMapping("/withdraw/{id}")
    public String showWithdraw(@PathVariable Long id, Model model) {
        Optional<Customer> customerOptional = customerService.findById(id);
        Customer customer = customerOptional.get();
        Withdraw withdraw = new Withdraw();
        withdraw.setCustomer(customer);
        model.addAttribute("withdraw", withdraw);
        return "customer/withdraw";
    }

    @PostMapping("/withdraw/{customerId}")
    public String withdraw(Model model, @PathVariable Long customerId
            , @ModelAttribute Withdraw withdraw, BindingResult bindingResult) {
        new Withdraw().validate(withdraw, bindingResult);
        Optional<Customer> customerOptional = customerService.findById(customerId);
        Customer customer = customerOptional.get();

        withdraw.setCustomer(customer);
        if (bindingResult.hasErrors()) {
            model.addAttribute("success", false);
            model.addAttribute("message", "Withdraw unsuccessful");
            model.addAttribute("error", true);
            model.addAttribute("checkWithdraw", true);
            model.addAttribute("withdraw", withdraw);
            return "customer/withdraw";
        }
        if (withdraw.getTransactionAmount().compareTo(customer.getBalance()) > 0) {
            model.addAttribute("success", false);
            model.addAttribute("message", "Insufficient balance to make a Withdraw");
            model.addAttribute("error", true);
            model.addAttribute("checkWithdraw", true);
            return "customer/withdraw";
        }
        customerService.withdraw(withdraw);

        withdraw.setTransactionAmount(null);
        Customer customerNew = customerService.findById(customerId).get();
        withdraw.setCustomer(customerNew);
        model.addAttribute("withdraw", withdraw);

        model.addAttribute("message", "Withdraw successfully ");
        model.addAttribute("success", true);
        return "customer/withdraw";
    }

    @GetMapping("/transfer/{senderId}")
    public String showTransferPage(@PathVariable Long senderId, Model model) {
        Optional<Customer> customerOptional = customerService.findById(senderId);
        Customer sender = customerOptional.get();
        List<Customer> recipients = customerService.findAllWithoutId(senderId);
        Transfer transfer = new Transfer();
        transfer.setSender(sender);
        model.addAttribute("transfer", transfer);
        model.addAttribute("recipients", recipients);

        return "customer/transfer";
    }

    @PostMapping("/transfer/{senderId}")
    public String transfer(@PathVariable Long senderId, @ModelAttribute Transfer transfer
            , BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {

        new Transfer().validate(transfer, bindingResult);
        List<Customer> recipients = customerService.findAllWithoutId(senderId);
        Optional<Customer> customerOptional = customerService.findById(senderId);
        Customer customer = customerOptional.get();
        Optional<Customer> customerRecipient = customerService.findById(transfer.getRecipient().getId());
        Customer recipient = customerRecipient.get();
        if (bindingResult.hasErrors()) {
            model.addAttribute("success", false);
            model.addAttribute("message", "Transfer unsuccessful");
            model.addAttribute("error", true);
            model.addAttribute("checkTransfer", true);
            model.addAttribute("transfer", transfer);
            model.addAttribute("recipients", recipients);

            return "customer/transfer";
        }
        if (transfer.getTransferAmount().compareTo(customer.getBalance()) > 0) {
            model.addAttribute("success", false);
            model.addAttribute("message", "Insufficient balance to make a Transfer");
            model.addAttribute("transfer", transfer);
            model.addAttribute("recipients", recipients);

            return "customer/transfer";
        }
        transfer.setSender(customer);
        transfer.setRecipient(recipient);
        customerService.transfer(transfer);
        redirectAttributes.addFlashAttribute("success", true);
        redirectAttributes.addFlashAttribute("message", "Transfer successful");

        return "redirect:/customers";

    }

    @GetMapping("/history-transfer")
    public String showHistoryTransferPage(Model model) {
        List<Customer> customers = customerService.findAll(false);
        List<Transfer> transfers = transferService.findAll(false);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        List<String> formattedTimes = new ArrayList<>();
        for (Transfer transfer : transfers) {
            LocalDateTime time = transfer.getDateTransfer();
            String formattedTime = time.format(formatter);
            transfer.setDateTransfer(LocalDateTime.parse(formattedTime, formatter));
            formattedTimes.add(formattedTime);
        }
        model.addAttribute("customers", customers);
        model.addAttribute("transfers", transfers);
        model.addAttribute("formattedTimes", formattedTimes);
        return "customer/history/historyTransfer";
    }

    @GetMapping("/history-deposit")
    public String showHistoryDepositPage(Model model) {
        List<Deposit> deposits = depositService.findAll(false);
        model.addAttribute("deposits", deposits);

        return "customer/history/historyDeposit";
    }

    @GetMapping("/history-withdraw")
    public String showHistoryWithdrawPage(Model model) {
        List<Withdraw> withdraws = withdrawService.findAll(false);
        model.addAttribute("withdraws", withdraws);

        return "customer/history/historyWithdraw";
    }


}
