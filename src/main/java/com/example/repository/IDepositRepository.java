package com.example.repository;

import com.example.model.Deposit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IDepositRepository extends JpaRepository<Deposit,Long> {

    Deposit findDepositByCustomerId(long customer_id);
}
