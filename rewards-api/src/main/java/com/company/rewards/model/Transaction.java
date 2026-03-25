package com.company.rewards.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "CUST_TRANSACTION")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Many transactions belong to one customer
    @ManyToOne
    @JoinColumn(name = "CUSTOMER_ID", nullable = false)
    @NotNull(message = "Customer is required")
    private Customer customer;

    @Min(value = 0, message = "Amount must be non-negative")
    @Column(nullable = false)
    private double amount;

    @NotNull(message = "Transaction date is required")
    @PastOrPresent(message = "Transaction date cannot be in the future")
    @Column(name = "TRANSACTION_DATE", nullable = false)
    private LocalDateTime transactionDate;
}