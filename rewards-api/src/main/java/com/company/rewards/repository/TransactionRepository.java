package com.company.rewards.repository;

import com.company.rewards.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findByCustomerId(Long customerId);

    List<Transaction> findByCustomer_IdAndTransactionDateBetween(Long customerId,
            LocalDateTime from,
            LocalDateTime to);
}