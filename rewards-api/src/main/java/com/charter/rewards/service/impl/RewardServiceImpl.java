package com.charter.rewards.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.charter.rewards.dto.RewardRequestDto;
import com.charter.rewards.dto.RewardResponseDto;
import com.charter.rewards.dto.TransactionDto;
import com.charter.rewards.exception.CustomerNotFoundException;
import com.charter.rewards.model.Customer;
import com.charter.rewards.model.Transaction;
import com.charter.rewards.repository.CustomerRepository;
import com.charter.rewards.repository.TransactionRepository;
import com.charter.rewards.service.RewardService;

@Service
public class RewardServiceImpl implements RewardService {

    private final CustomerRepository customerRepository;
    private final TransactionRepository transactionRepository;

    private static final DateTimeFormatter MONTH_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM");
    private static final BigDecimal HUNDRED = BigDecimal.valueOf(100);
    private static final BigDecimal FIFTY = BigDecimal.valueOf(50);

    public RewardServiceImpl(CustomerRepository customerRepository,
                             TransactionRepository transactionRepository) {
        this.customerRepository = customerRepository;
        this.transactionRepository = transactionRepository;
    }

    @Override
    public Optional<RewardResponseDto> getRewardsForCustomer(Long customerId, RewardRequestDto request) {
    	Customer customer = customerRepository.findById(customerId)
    	        .orElseThrow(() -> new CustomerNotFoundException(customerId));

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start;
        LocalDateTime end = now;

        if (request.getFrom() != null && request.getTo() != null) {
            start = request.getFrom().atStartOfDay();
            end = request.getTo().atTime(LocalTime.MAX); // full day coverage
        } else if (request.getMonths() != null && request.getMonths() > 0) {
            start = now.minusMonths(request.getMonths());
        } else {
            start = now.minusMonths(3); // default window
        }

        List<Transaction> transactions =
                transactionRepository.findByCustomer_IdAndTransactionDateBetween(customerId, start, end);

        Map<String, Integer> monthlyPoints = new HashMap<>();
        List<TransactionDto> transactionDTOs = new ArrayList<>();

        for (Transaction t : transactions) {
            int points = calculatePoints(t.getAmount());
            String month = t.getTransactionDate().format(MONTH_FORMATTER);
            monthlyPoints.merge(month, points, Integer::sum);

            transactionDTOs.add(new TransactionDto(
                    t.getId(),
                    t.getAmount(),
                    t.getTransactionDate(),
                    points
            ));
        }

        int totalPoints = monthlyPoints.values()
                                       .stream()
                                       .mapToInt(Integer::intValue)
                                       .sum();
        
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
        if (amount.compareTo(HUNDRED) > 0) {
            return amount.subtract(HUNDRED)
                         .setScale(0, RoundingMode.UP)
                         .multiply(BigDecimal.valueOf(2))
                         .intValueExact() + 50;
        } else if (amount.compareTo(FIFTY) > 0) {
            return amount.subtract(FIFTY)
                         .setScale(0, RoundingMode.UP)
                         .intValueExact();
        }
        return 0;
    }
}