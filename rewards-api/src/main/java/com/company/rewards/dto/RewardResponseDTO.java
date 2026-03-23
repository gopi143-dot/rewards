package com.company.rewards.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class RewardResponseDTO {
    private Long customerId;
    private String customerName;
    private String customerEmail;
    private Map<String, Integer> monthlyPoints; // month-year -> points
    private int totalPoints;
    private List<TransactionDTO> transactions;

    public RewardResponseDTO() {}

    public RewardResponseDTO(Long customerId, String customerName, String customerEmail, Map<String, Integer> monthlyPoints, int totalPoints, List<TransactionDTO> transactions) {
        this.customerId = customerId;
        this.customerName = customerName;
        this.customerEmail = customerEmail;
        this.monthlyPoints = monthlyPoints;
        this.totalPoints = totalPoints;
        this.transactions = transactions;
    }
}