package com.company.rewards.service.impl;

import com.company.rewards.dto.RewardRequestDto;
import com.company.rewards.dto.RewardResponseDto;
import com.company.rewards.dto.TransactionDto;
import com.company.rewards.model.Customer;
import com.company.rewards.model.Transaction;
import com.company.rewards.repository.CustomerRepository;
import com.company.rewards.repository.TransactionRepository;
import com.company.rewards.service.RewardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class RewardServiceImpl implements RewardService {

    private final CustomerRepository customerRepository;
    private final TransactionRepository transactionRepository;

    private static final DateTimeFormatter MONTH_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM");

    @Autowired
    public RewardServiceImpl(CustomerRepository customerRepository,
                             TransactionRepository transactionRepository) {
        this.customerRepository = customerRepository;
        this.transactionRepository = transactionRepository;
    }

    @Override
    public Optional<RewardResponseDto> getRewardsForCustomer(RewardRequestDto request) {
        Optional<Customer> customerOpt = customerRepository.findById(request.getCustomerId());

        if (customerOpt.isEmpty()) {
            return Optional.empty();
        }

        Customer customer = customerOpt.get();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start;
        LocalDateTime end=now;

        if (request.getFrom() != null && request.getTo() != null) {
            start = request.getFrom().atStartOfDay();
            end = request.getTo().atTime(LocalTime.MAX); // full day coverage
        } else if (request.getMonths() != null && request.getMonths() > 0) {
            start = now.minusMonths(request.getMonths());
        } else {
            start = now.minusMonths(3); // default window
        }

        List<Transaction> transactions =
                transactionRepository.findByCustomer_IdAndTransactionDateBetween(request.getCustomerId(), start, end);

        Map<String, Integer> monthlyPoints = new HashMap<>();
        int totalPoints = 0;
        List<TransactionDto> transactionDTOs = new ArrayList<>();

        for (Transaction t : transactions) {
            int points = calculatePoints(BigDecimal.valueOf(t.getAmount()));
            String month = t.getTransactionDate().format(MONTH_FORMATTER);
            monthlyPoints.merge(month, points, Integer::sum);
            totalPoints += points;
            transactionDTOs.add(new TransactionDto(
                    t.getId(),
                    t.getAmount(),
                    t.getTransactionDate(),
                    points
            ));
        }

        RewardResponseDto response = new RewardResponseDto(
                customer.getId(),
                customer.getName(),
                customer.getEmail(),
                monthlyPoints,
                totalPoints,
                transactionDTOs
        );

        return Optional.of(response);
    }

    private int calculatePoints(BigDecimal amount) {
        BigDecimal hundred = BigDecimal.valueOf(100);
        BigDecimal fifty = BigDecimal.valueOf(50);

        if (amount.compareTo(hundred) > 0) {
            // Above 100: 2 points per dollar + 50 points for 50–100 range
            return amount.subtract(hundred).multiply(BigDecimal.valueOf(2)).intValue() + 50;
        } else if (amount.compareTo(fifty) > 0) {
            // Between 50 and 100: 1 point per dollar
            return amount.subtract(fifty).intValue();
        }
        return 0;
    }
}