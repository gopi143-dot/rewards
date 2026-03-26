package com.company.rewards.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor   // generates a constructor with all fields
@NoArgsConstructor 
public class RewardResponseDto {
    private Long customerId;
    private String customerName;
    private String customerEmail;
    private Map<String, Integer> monthlyPoints; // month-year -> points
    private int totalPoints;
    private List<TransactionDto> transactions;

}