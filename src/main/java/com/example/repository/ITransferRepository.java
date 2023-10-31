package com.example.repository;

import com.example.model.Deposit;
import com.example.model.Transfer;
import com.example.model.Withdraw;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ITransferRepository extends JpaRepository<Transfer, Long> {


}
