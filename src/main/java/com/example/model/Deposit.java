package com.example.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
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
@Table(name = "deposits")
public class Deposit implements Validator {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "customer_id", referencedColumnName = "id", nullable = false)
    private Customer customer;

    @Column(name ="transaction_amount", precision = 10,scale = 2, nullable = false)
    private BigDecimal transactionAmount;
    private Boolean deleted = false;

    @Column(name = "time_deposit")
    private LocalDateTime localDateTime = LocalDateTime.now();


    @Override
    public boolean supports(Class<?> aClass) {
        return false;
    }

    @Override
    public void validate(Object o, Errors errors) {
        Deposit deposit = (Deposit) o;
        BigDecimal transactionAmount = deposit.transactionAmount;
        if (transactionAmount == null || transactionAmount.compareTo(BigDecimal.valueOf(1000)) < 0) {
            errors.rejectValue("transactionAmount", "deposit.transactionAmount.min");
            return;
        }
        if (transactionAmount.compareTo(BigDecimal.valueOf(1000000000)) > 0) {
            errors.rejectValue("transactionAmount", "deposit.transactionAmount.max");
        }


    }
}
