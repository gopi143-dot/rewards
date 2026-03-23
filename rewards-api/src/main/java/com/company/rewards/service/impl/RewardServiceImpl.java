package com.company.rewards.service.impl;

import com.company.rewards.dto.RewardResponseDTO;
import com.company.rewards.dto.TransactionDTO;
import com.company.rewards.model.Customer;
import com.company.rewards.model.Transaction;
import com.company.rewards.repository.CustomerRepository;
import com.company.rewards.repository.TransactionRepository;
import com.company.rewards.service.RewardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
public class RewardServiceImpl implements RewardService {
    private final CustomerRepository customerRepository;
    private final TransactionRepository transactionRepository;

    @Autowired
    public RewardServiceImpl(CustomerRepository customerRepository, TransactionRepository transactionRepository) {
        this.customerRepository = customerRepository;
        this.transactionRepository = transactionRepository;
    }

    @Override
    public Optional<RewardResponseDTO> getRewardsForCustomer(Long customerId, Integer months, LocalDate from, LocalDate to) {
        Optional<Customer> customerOpt = customerRepository.findById(customerId);
        if (!customerOpt.isPresent()) {
            return Optional.empty();
        }
        Customer customer = customerOpt.get();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start;
        LocalDateTime end = now;
        if (from != null && to != null) {
            start = from.atStartOfDay();
            end = to.atTime(23, 59, 59);
        } else if (months != null && months > 0) {
            start = now.minusMonths(months);
        } else {
            start = now.minusMonths(3);
        }
        List<Transaction> transactions = transactionRepository.findByCustomerIdAndDateRange(customerId, start, end);
        Map<String, Integer> monthlyPoints = new HashMap<>();
        int totalPoints = 0;
        List<TransactionDTO> transactionDTOs = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
        for (Transaction t : transactions) {
            int points = calculatePoints(t.getAmount());
            String month = t.getTransactionDate().format(formatter);
            monthlyPoints.put(month, monthlyPoints.getOrDefault(month, 0) + points);
            totalPoints += points;
            transactionDTOs.add(new TransactionDTO(t.getId(), t.getAmount(), t.getTransactionDate(), points));
        }
        RewardResponseDTO response = new RewardResponseDTO(
                customer.getId(),
                customer.getName(),
                customer.getEmail(),
                monthlyPoints,
                totalPoints,
                transactionDTOs
        );
        return Optional.of(response);
    }

    private int calculatePoints(double amount) {
        int points = 0;
        if (amount > 100) {
            points += (int) ((amount - 100) * 2);
            points += 50; // 1 point for each dollar between 50 and 100
        } else if (amount > 50) {
            points += (int) (amount - 50);
        }
        return points;
    }
}
