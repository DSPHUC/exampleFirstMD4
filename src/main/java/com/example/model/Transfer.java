package com.example.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "transfers")
public class Transfer implements Validator {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    private Customer sender;

    @OneToOne
    private Customer recipient;

    private BigDecimal transferAmount;
    private Long fees;
    private BigDecimal feesAmount;
    private BigDecimal transactionAmount;

    private LocalDateTime dateTransfer = LocalDateTime.now();
    private Boolean deleted = false;


    @Override
    public boolean supports(Class<?> aClass) {
        return false;
    }

    @Override
    public void validate(Object o, Errors errors) {
        Transfer transfer = (Transfer) o;
        BigDecimal transferAmount = transfer.transferAmount;

        if (transferAmount == null) {
            errors.rejectValue("transfer","transfer.transferAmount.null");
            return;
        }
        if (transferAmount.compareTo(BigDecimal.valueOf(1000)) < 0) {
            errors.rejectValue("transfer","transfer.transferAmount.min");
            return;
        }if (transferAmount.compareTo(transfer.sender.getBalance()) > 0) {
            errors.rejectValue("transfer","transfer.transferAmount.max");
        }
    }
}
