package com.company.rewards.service;

import com.company.rewards.dto.RewardResponseDTO;
import com.company.rewards.model.Customer;
import com.company.rewards.model.Transaction;
import com.company.rewards.repository.CustomerRepository;
import com.company.rewards.repository.TransactionRepository;
import com.company.rewards.service.impl.RewardServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

class RewardServiceImplTest {
    private CustomerRepository customerRepository;
    private TransactionRepository transactionRepository;
    private RewardService rewardService;

    @BeforeEach
    void setUp() {
        customerRepository = Mockito.mock(CustomerRepository.class);
        transactionRepository = Mockito.mock(TransactionRepository.class);
        rewardService = new RewardServiceImpl(customerRepository, transactionRepository);
    }

    @Test
    void testRewardCalculationForCustomer() {
        Customer customer = new Customer(1L, "Test User", "test@example.com");
        Mockito.when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        Mockito.when(transactionRepository.findByCustomerIdAndDateRange(Mockito.eq(1L), Mockito.any(), Mockito.any()))
                .thenReturn(Arrays.asList(
                        new Transaction(1L, 1L, 120.0, LocalDateTime.now().minusMonths(1)),
                        new Transaction(2L, 1L, 75.0, LocalDateTime.now().minusMonths(2))
                ));
        Optional<RewardResponseDTO> response = rewardService.getRewardsForCustomer(1L, 3, null, null);
        Assertions.assertTrue(response.isPresent());
        Assertions.assertEquals(1L, response.get().getCustomerId());
        Assertions.assertEquals(90 + 25, response.get().getTotalPoints());
    }

    @Test
    void testCustomerNotFound() {
        Mockito.when(customerRepository.findById(2L)).thenReturn(Optional.empty());
        Optional<RewardResponseDTO> response = rewardService.getRewardsForCustomer(2L, 3, null, null);
        Assertions.assertFalse(response.isPresent());
    }
}
