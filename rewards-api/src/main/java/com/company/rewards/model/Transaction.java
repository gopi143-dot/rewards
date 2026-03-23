package com.company.rewards.model;

import lombok.Data;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "TRANSACTION")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "CUSTOMER_ID")
    private Long customerId;
    private double amount;
    @Column(name = "TRANSACTION_DATE")
    private LocalDateTime transactionDate;

    public Transaction(Long id, Long customerId, double amount, LocalDateTime transactionDate) {
        this.id = id;
        this.customerId = customerId;
        this.amount = amount;
        this.transactionDate = transactionDate;
    }

    public Transaction() {}
}