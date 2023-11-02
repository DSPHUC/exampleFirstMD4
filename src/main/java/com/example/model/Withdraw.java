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

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "withdraws")
public class Withdraw implements Validator {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "customer_id", referencedColumnName = "id", nullable = false)
    private Customer customer;

    @Column(precision = 10,scale = 2, nullable = false)
    private BigDecimal transactionAmount;
    private Boolean deleted = false;
    private LocalDateTime localDateTime = LocalDateTime.now();


    @Override
    public boolean supports(Class<?> aClass) {
        return false;
    }

    @Override
    public void validate(Object o, Errors errors) {
        Withdraw withdraw = (Withdraw) o;
        BigDecimal transactionAmount = withdraw.transactionAmount;

        if (transactionAmount == null || transactionAmount.compareTo(BigDecimal.valueOf(1000)) < 0) {
            errors.rejectValue("transactionAmount", "withdraw.transactionAmount.min");
            return;
        }
        if (transactionAmount.compareTo(BigDecimal.valueOf(1000000000)) > 0) {
            errors.rejectValue("transactionAmount", "withdraw.transactionAmount.max");

        }

    }
}
