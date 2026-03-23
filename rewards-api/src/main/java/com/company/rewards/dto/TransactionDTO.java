package com.company.rewards.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor   // generates a constructor with all fields
@NoArgsConstructor 
public class TransactionDTO {
    private Long id;
    private double amount;
    private LocalDateTime transactionDate;
    private int rewardPoints;
}