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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/customers")
@Transactional
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
    public ModelAndView listBan(@ModelAttribute Customer customer) {
        ModelAndView modelAndView = new ModelAndView("customer/listBan");
        List<Customer> customers = customerService.findAll(true);
        modelAndView.addObject("customerBan", customers);
        return modelAndView;
    }

    @PostMapping("/listBan")
    public String listBanCustomer(@ModelAttribute Customer customer, Model model) {

        return null;
    }

    @GetMapping("/create")
    public String showCreateCustomer(Model model) {
        model.addAttribute("customer", new Customer());
        return "customer/create";
    }

    @PostMapping("/create")
    public String createCustomer(@ModelAttribute Customer customer, Model model) {
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

    @PostMapping("/update/{id}")
    public String updateCustomer(Model model, @PathVariable Long id, @ModelAttribute Customer customer) {
        Optional<Customer> customerOptional = customerService.findById(id);
        Customer customer1 = customerOptional.get();
        customerService.update(id, customer1);
        model.addAttribute("message", "Updated successfully");
        model.addAttribute("success", true);
        return "customer/edit";
    }

    @GetMapping("/listBan/{id}")
    public String restore(@PathVariable long id, RedirectAttributes redirectAttributes) {
        customerService.restore(id);
        redirectAttributes.addFlashAttribute("success", true);
        redirectAttributes.addFlashAttribute("message", "Unban successfully");
        return "redirect:";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable long id, RedirectAttributes redirectAttributes) {
        customerService.removeById(id);
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

    @PostMapping("/deposit/{id}")
    public String deposit(Model model, @PathVariable Long id, @ModelAttribute Deposit deposit) {
        Optional<Customer> customerOptional = customerService.findById(id);
        Customer customer = customerOptional.get();

        deposit.setCustomer(customer);
        customerService.deposit(deposit);

        deposit.setTransactionAmount(null);

        model.addAttribute("deposit", deposit);

        model.addAttribute("message", "Deposit successfully is" + "money");
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

    @PostMapping("/withdraw/{id}")
    public String withdraw(Model model, @PathVariable Long id, @ModelAttribute Withdraw withdraw) {
        Optional<Customer> customerOptional = customerService.findById(id);
        Customer customer = customerOptional.get();
        withdraw.setCustomer(customer);
        customerService.withdraw(withdraw);

        withdraw.setTransactionAmount(null);

        model.addAttribute("withdraw", withdraw);

        model.addAttribute("message", "Withdraw successfully is" + "money");
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
            , Model model, RedirectAttributes redirectAttributes) {
        List<Customer> recipients = customerService.findAllWithoutId(senderId);
        Optional<Customer> customerOptional = customerService.findById(senderId);
        Customer customer = customerOptional.get();
        Optional<Customer> customerRecipient = customerService.findById(transfer.getRecipient().getId());
        Customer recipient = customerRecipient.get();
        transfer.setSender(customer);
        transfer.setRecipient(recipient);

        if (transfer.getTransferAmount().compareTo(BigDecimal.ZERO) == 0) {
            model.addAttribute("success", false);
            model.addAttribute("message", "Transfer amount must be greater than 0");
            model.addAttribute("transfer", transfer);
            model.addAttribute("recipients", recipients);

            return "customer/transfer";
        } else if (transfer.getTransferAmount().compareTo(customer.getBalance()) > 0) {
            model.addAttribute("success", false);
            model.addAttribute("message", "Insufficient balance to make a Transfer");
            model.addAttribute("transfer", transfer);
            model.addAttribute("recipients", recipients);

            return "customer/transfer";
        } else {
            customerService.transfer(transfer);
            redirectAttributes.addFlashAttribute("success", true);
            redirectAttributes.addFlashAttribute("message", "Transfer successful");

            return "redirect:/customers";
        }
    }

    @GetMapping("/history-transfer")
    public String showHistoryTransferPage(Model model) {
        List<Transfer> transfers = transferService.findAll(false);
        model.addAttribute("transfers", transfers);

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
