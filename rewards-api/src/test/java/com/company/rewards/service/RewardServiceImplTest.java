package com.company.rewards.service;

import com.company.rewards.dto.RewardRequestDto;
import com.company.rewards.dto.RewardResponseDto;
import com.company.rewards.dto.TransactionDto;
import com.company.rewards.model.Customer;
import com.company.rewards.model.Transaction;
import com.company.rewards.repository.CustomerRepository;
import com.company.rewards.repository.TransactionRepository;
import com.company.rewards.service.impl.RewardServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class RewardServiceImplTest {

    private CustomerRepository customerRepository;
    private TransactionRepository transactionRepository;
    private RewardServiceImpl rewardService;

    @BeforeEach
    void setUp() {
        customerRepository = Mockito.mock(CustomerRepository.class);
        transactionRepository = Mockito.mock(TransactionRepository.class);
        rewardService = new RewardServiceImpl(customerRepository, transactionRepository);
    }

    @Test
    void testCustomerNotFound() {
        RewardRequestDto request = new RewardRequestDto();
        request.setCustomerId(99L);
        request.setMonths(3);

        Mockito.when(customerRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<RewardResponseDto> response = rewardService.getRewardsForCustomer(request);
        assertFalse(response.isPresent());
    }

    @Test
    void testRewardCalculationWithMonths() {
        Customer customer = new Customer();
        customer.setId(1L);
        customer.setName("Test User");
        customer.setEmail("test@example.com");

        Transaction t1 = new Transaction();
        t1.setId(1L);
        t1.setCustomer(customer);
        t1.setAmount(120.0);
        t1.setTransactionDate(LocalDateTime.now().minusMonths(1));

        Transaction t2 = new Transaction();
        t2.setId(2L);
        t2.setCustomer(customer);
        t2.setAmount(75.0);
        t2.setTransactionDate(LocalDateTime.now().minusMonths(2));

        Mockito.when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        Mockito.when(transactionRepository.findByCustomer_IdAndTransactionDateBetween(
                Mockito.eq(1L), Mockito.any(), Mockito.any()))
                .thenReturn(Arrays.asList(t1, t2));

        RewardRequestDto request = new RewardRequestDto();
        request.setCustomerId(1L);
        request.setMonths(3);

        Optional<RewardResponseDto> response = rewardService.getRewardsForCustomer(request);

        assertTrue(response.isPresent());
        assertEquals(115, response.get().getTotalPoints()); // 90 + 25
    }

    @Test
    void testRewardCalculationWithDateRange() {
        Customer customer = new Customer();
        customer.setId(2L);
        customer.setName("Alice");
        customer.setEmail("alice@example.com");

        LocalDate from = LocalDate.now().minusDays(10);
        LocalDate to = LocalDate.now();

        Transaction t1 = new Transaction();
        t1.setId(3L);
        t1.setCustomer(customer);
        t1.setAmount(200.0);
        t1.setTransactionDate(LocalDateTime.now().minusDays(5));

        Mockito.when(customerRepository.findById(2L)).thenReturn(Optional.of(customer));
        Mockito.when(transactionRepository.findByCustomer_IdAndTransactionDateBetween(
                Mockito.eq(2L), Mockito.any(), Mockito.any()))
                .thenReturn(Collections.singletonList(t1));

        RewardRequestDto request = new RewardRequestDto();
        request.setCustomerId(2L);
        request.setFrom(from);
        request.setTo(to);

        Optional<RewardResponseDto> response = rewardService.getRewardsForCustomer(request);

        assertTrue(response.isPresent());
        assertEquals(250, response.get().getTotalPoints()); // (200-100)*2 + 50
    }

    @Test
    void testDefaultWindowWhenNoMonthsOrDates() {
        Customer customer = new Customer();
        customer.setId(3L);
        customer.setName("Bob");
        customer.setEmail("bob@example.com");

        Transaction t1 = new Transaction();
        t1.setId(4L);
        t1.setCustomer(customer);
        t1.setAmount(60.0);
        t1.setTransactionDate(LocalDateTime.now().minusMonths(2));

        Mockito.when(customerRepository.findById(3L)).thenReturn(Optional.of(customer));
        Mockito.when(transactionRepository.findByCustomer_IdAndTransactionDateBetween(
                Mockito.eq(3L), Mockito.any(), Mockito.any()))
                .thenReturn(Collections.singletonList(t1));

        RewardRequestDto request = new RewardRequestDto();
        request.setCustomerId(3L);

        Optional<RewardResponseDto> response = rewardService.getRewardsForCustomer(request);

        assertTrue(response.isPresent());
        assertEquals(10, response.get().getTotalPoints()); // 60 → 10 points
    }

    @Test
    void testCalculatePointsBelow50() {
        Customer customer = new Customer();
        customer.setId(4L);
        customer.setName("Charlie");
        customer.setEmail("charlie@example.com");

        Transaction t1 = new Transaction();
        t1.setId(5L);
        t1.setCustomer(customer);
        t1.setAmount(40.0);
        t1.setTransactionDate(LocalDateTime.now());

        Mockito.when(customerRepository.findById(4L)).thenReturn(Optional.of(customer));
        Mockito.when(transactionRepository.findByCustomer_IdAndTransactionDateBetween(
                Mockito.eq(4L), Mockito.any(), Mockito.any()))
                .thenReturn(Collections.singletonList(t1));

        RewardRequestDto request = new RewardRequestDto();
        request.setCustomerId(4L);
        request.setMonths(1);

        Optional<RewardResponseDto> response = rewardService.getRewardsForCustomer(request);

        assertTrue(response.isPresent());
        assertEquals(0, response.get().getTotalPoints()); // below 50 → 0 points
    }

    @Test
    void testRewardCalculationWithFractionalAmount() {
        Customer customer = new Customer();
        customer.setId(5L);
        customer.setName("Daisy");
        customer.setEmail("daisy@example.com");

        Transaction t1 = new Transaction();
        t1.setId(6L);
        t1.setCustomer(customer);
        t1.setAmount(120.75);
        t1.setTransactionDate(LocalDateTime.now());

        Mockito.when(customerRepository.findById(5L)).thenReturn(Optional.of(customer));
        Mockito.when(transactionRepository.findByCustomer_IdAndTransactionDateBetween(
                Mockito.eq(5L), Mockito.any(), Mockito.any()))
                .thenReturn(Collections.singletonList(t1));

        RewardRequestDto request = new RewardRequestDto();
        request.setCustomerId(5L);
        request.setMonths(1);

        Optional<RewardResponseDto> response = rewardService.getRewardsForCustomer(request);

        assertTrue(response.isPresent());
        assertEquals(91, response.get().getTotalPoints()); // (120.75-100)*2 + 50 = 91.5 → intValue() = 91
        TransactionDto dto = response.get().getTransactions().get(0);
        assertEquals(91, dto.getRewardPoints());
    }
}