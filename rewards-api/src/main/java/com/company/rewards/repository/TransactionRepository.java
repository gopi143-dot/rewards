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

    @Query("SELECT t FROM Transaction t WHERE t.customerId = :customerId AND t.transactionDate BETWEEN :from AND :to")
    List<Transaction> findByCustomerIdAndDateRange(@Param("customerId") Long customerId, @Param("from") LocalDateTime from, @Param("to") LocalDateTime to);
}