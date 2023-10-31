package com.example.repository;

import com.example.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Repository
public interface ICustomerRepository extends JpaRepository<Customer, Long> {
    @Query("update Customer  as c SET c.balance = c.balance - :amount where c.id = :id")
    @Modifying
    void reduceBalance(@Param("id") Long id, @Param("amount") BigDecimal amount);

    @Query("update Customer as c SET c.balance = c.balance + :amount where c.id = :id")
    @Modifying
    void incrementBalance(@Param("id") Long id, @Param("amount") BigDecimal amount);
}
