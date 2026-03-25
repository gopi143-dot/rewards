package com.company.rewards.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class RewardResponseDto {
    private Long customerId;
    private String customerName;
    private String customerEmail;
    private Map<String, Integer> monthlyPoints; // month-year -> points
    private int totalPoints;
    private List<TransactionDto> transactions;

    public RewardResponseDto() {}

    public RewardResponseDto(Long customerId, String customerName, String customerEmail, Map<String, Integer> monthlyPoints, int totalPoints, List<TransactionDto> transactions) {
        this.customerId = customerId;
        this.customerName = customerName;
        this.customerEmail = customerEmail;
        this.monthlyPoints = monthlyPoints;
        this.totalPoints = totalPoints;
        this.transactions = transactions;
    }
}