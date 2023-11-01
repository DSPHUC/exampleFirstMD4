package com.example.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import javax.persistence.*;
import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "customers")
public class Customer implements Validator {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "full_name", nullable = false, unique = true)
    private String fullName;


    private String email;
    private String phone;
    private String address;

    @Column(updatable = false)
    private BigDecimal balance;


    private Boolean deleted = false;


    @Override
    public boolean supports(Class<?> aClass) {
        return false;
    }

    @Override
    public void validate(Object o, Errors errors) {
        Customer customer = (Customer) o;
        String fullName = customer.fullName;
        String email = customer.email;
        String phone = customer.phone;
        String address = customer.address;
        if (fullName.isEmpty()||fullName == null || fullName.length() < 3) {
            errors.rejectValue("fullName", "customer.fullName.notNull"
                    , "fullName Not empty or less than 3");
            return;
        }
        if (!fullName.matches("^[a-zA-Z\\s]+$")) {
            errors.rejectValue("fullName", "customer.fullName.invalidFormat"
                    , "Full name must contain only letters");
            return;
        }
        if (phone == null || phone.length() != 10 || !phone.matches("\\d+")) {
            errors.rejectValue("phone", "customer.phone.validate"
                    , "phone is number and phone number is 10 number");
            return;
        }
        if (email == null || !email.matches("^[a-zA-Z0-9]+.+[a-zA-Z0-9]+@[a-zA-Z0-9]+\\.[a-zA-Z]{2,}$")) {
            errors.rejectValue("email", "customer.email.validate");
            return;
        }
        if (address == null||address.isEmpty()) {
            errors.rejectValue("phone", "customer.address.notNull");
        }

    }
}
