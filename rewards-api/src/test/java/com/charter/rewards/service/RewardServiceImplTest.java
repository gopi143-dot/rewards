package com.charter.rewards.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.charter.rewards.dto.RewardRequestDto;
import com.charter.rewards.dto.RewardResponseDto;
import com.charter.rewards.exception.CustomerNotFoundException;
import com.charter.rewards.model.Customer;
import com.charter.rewards.model.Transaction;
import com.charter.rewards.repository.CustomerRepository;
import com.charter.rewards.repository.TransactionRepository;
import com.charter.rewards.service.impl.RewardServiceImpl;

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

    private Customer createCustomer(Long id, String name, String email) {
        Customer customer = new Customer();
        customer.setId(id);
        customer.setName(name);
        customer.setEmail(email);
        return customer;
    }

    private Transaction createTransaction(Long id, Customer customer, BigDecimal amount, LocalDateTime date) {
        Transaction t = new Transaction();
        t.setId(id);
        t.setCustomer(customer);
        t.setAmount(amount);
        t.setTransactionDate(date);
        return t;
    }

    @Test
    void testCustomerNotFound() {
        Mockito.when(customerRepository.findById(99L)).thenReturn(Optional.empty());

        RewardRequestDto request = new RewardRequestDto();
        request.setMonths(3);

        assertThrows(CustomerNotFoundException.class,
                () -> rewardService.getRewardsForCustomer(99L, request));
    }

    @Test
    void testRewardCalculationWithMonths() {
        Customer customer = createCustomer(1L, "Test User", "test@example.com");
        Transaction t1 = createTransaction(1L, customer, BigDecimal.valueOf(120), LocalDateTime.now().minusMonths(1));
        Transaction t2 = createTransaction(2L, customer, BigDecimal.valueOf(75), LocalDateTime.now().minusMonths(2));

        Mockito.when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        Mockito.when(transactionRepository.findByCustomer_IdAndTransactionDateBetween(
                Mockito.eq(1L), Mockito.any(), Mockito.any()))
                .thenReturn(Arrays.asList(t1, t2));

        RewardRequestDto request = new RewardRequestDto();
        request.setMonths(3);

        Optional<RewardResponseDto> response = rewardService.getRewardsForCustomer(1L, request);
        assertEquals(115, response.get().getTotalPoints()); // 90 + 25
    }

    @Test
    void testRewardCalculationWithDateRange() {
        Customer customer = createCustomer(2L, "Alice", "alice@example.com");
        Transaction t1 = createTransaction(3L, customer, BigDecimal.valueOf(200), LocalDateTime.now().minusDays(5));

        Mockito.when(customerRepository.findById(2L)).thenReturn(Optional.of(customer));
        Mockito.when(transactionRepository.findByCustomer_IdAndTransactionDateBetween(
                Mockito.eq(2L), Mockito.any(), Mockito.any()))
                .thenReturn(Collections.singletonList(t1));

        RewardRequestDto request = new RewardRequestDto();
        request.setFrom(LocalDate.now().minusDays(10));
        request.setTo(LocalDate.now());

        Optional<RewardResponseDto> response = rewardService.getRewardsForCustomer(2L, request);
        assertEquals(250, response.get().getTotalPoints()); // (200-100)*2 + 50
    }

    @Test
    void testDefaultWindowWhenNoMonthsOrDates() {
        Customer customer = createCustomer(3L, "Bob", "bob@example.com");
        Transaction t1 = createTransaction(4L, customer, BigDecimal.valueOf(60), LocalDateTime.now().minusMonths(2));

        Mockito.when(customerRepository.findById(3L)).thenReturn(Optional.of(customer));
        Mockito.when(transactionRepository.findByCustomer_IdAndTransactionDateBetween(
                Mockito.eq(3L), Mockito.any(), Mockito.any()))
                .thenReturn(Collections.singletonList(t1));

        RewardRequestDto request = new RewardRequestDto();

        Optional<RewardResponseDto> response = rewardService.getRewardsForCustomer(3L, request);
        assertEquals(10, response.get().getTotalPoints()); // 60 → 10 points
    }

    @Test
    void testCalculatePointsBelow50() {
        Customer customer = createCustomer(4L, "Charlie", "charlie@example.com");
        Transaction t1 = createTransaction(5L, customer, BigDecimal.valueOf(40), LocalDateTime.now());

        Mockito.when(customerRepository.findById(4L)).thenReturn(Optional.of(customer));
        Mockito.when(transactionRepository.findByCustomer_IdAndTransactionDateBetween(
                Mockito.eq(4L), Mockito.any(), Mockito.any()))
                .thenReturn(Collections.singletonList(t1));

        RewardRequestDto request = new RewardRequestDto();
        request.setMonths(1);

        Optional<RewardResponseDto> response = rewardService.getRewardsForCustomer(4L, request);
        assertEquals(0, response.get().getTotalPoints()); // below 50 → 0 points
    }

    @Test
    void testRewardCalculationWithFractionalAmount() {
        Customer customer = createCustomer(5L, "Daisy", "daisy@example.com");
        Transaction t1 = createTransaction(6L, customer, BigDecimal.valueOf(120.75), LocalDateTime.now());

        Mockito.when(customerRepository.findById(5L)).thenReturn(Optional.of(customer));
        Mockito.when(transactionRepository.findByCustomer_IdAndTransactionDateBetween(
                Mockito.eq(5L), Mockito.any(), Mockito.any()))
                .thenReturn(Collections.singletonList(t1));

        RewardRequestDto request = new RewardRequestDto();
        request.setMonths(1);

        Optional<RewardResponseDto> response = rewardService.getRewardsForCustomer(5L, request);
        assertEquals(92, response.get().getTotalPoints()); // (120.75-100)*2 + 50 = 91.5 → 92
        assertEquals(92, response.get().getTransactions().get(0).getRewardPoints());
    }

    @Test
    void testRewardCalculationAt100Boundary() {
        Customer customer = createCustomer(6L, "Boundary User", "boundary@example.com");
        Transaction t1 = createTransaction(7L, customer, new BigDecimal("100.00"), LocalDateTime.now());

        Mockito.when(customerRepository.findById(6L)).thenReturn(Optional.of(customer));
        Mockito.when(transactionRepository.findByCustomer_IdAndTransactionDateBetween(
                Mockito.eq(6L), Mockito.any(), Mockito.any()))
                .thenReturn(Collections.singletonList(t1));

        RewardRequestDto request = new RewardRequestDto();
        request.setMonths(1);

        Optional<RewardResponseDto> response = rewardService.getRewardsForCustomer(6L, request);
        assertEquals(50, response.get().getTotalPoints()); // boundary case
    }

    @Test
    void testExtremelyLargeAmount() {
        Customer customer = createCustomer(7L, "Big Spender", "big@example.com");
        Transaction t1 = createTransaction(8L, customer, BigDecimal.valueOf(999999999.99), LocalDateTime.now());

        Mockito.when(customerRepository.findById(7L)).thenReturn(Optional.of(customer));
        Mockito.when(transactionRepository.findByCustomer_IdAndTransactionDateBetween(
                Mockito.eq(7L), Mockito.any(), Mockito.any()))
                .thenReturn(Collections.singletonList(t1));

        RewardRequestDto request = new RewardRequestDto();
        request.setMonths(1);

        Optional<RewardResponseDto> response = rewardService.getRewardsForCustomer(7L, request);
        assertEquals(1999999850,response.get().getTotalPoints()); // sanity check
    }
    
    @Test
    void testCustomerWithNoTransactions() {
        Customer customer = createCustomer(10L, "NoTxn User", "notxn@example.com");

        Mockito.when(customerRepository.findById(10L)).thenReturn(Optional.of(customer));
        Mockito.when(transactionRepository.findByCustomer_IdAndTransactionDateBetween(
                Mockito.eq(10L), Mockito.any(), Mockito.any()))
                .thenReturn(Collections.emptyList());

        RewardRequestDto request = new RewardRequestDto();
        request.setMonths(3);

        Optional<RewardResponseDto> response = rewardService.getRewardsForCustomer(10L, request);
        assertEquals(0, response.get().getTotalPoints());
        assertTrue(response.get().getTransactions().isEmpty());
    }

    @Test
    void testRewardCalculationAt50Boundary() {
        Customer customer = createCustomer(11L, "Fifty User", "fifty@example.com");
        Transaction t1 = createTransaction(11L, customer, new BigDecimal("50.00"), LocalDateTime.now());

        Mockito.when(customerRepository.findById(11L)).thenReturn(Optional.of(customer));
        Mockito.when(transactionRepository.findByCustomer_IdAndTransactionDateBetween(
                Mockito.eq(11L), Mockito.any(), Mockito.any()))
                .thenReturn(Collections.singletonList(t1));

        RewardRequestDto request = new RewardRequestDto();
        request.setMonths(1);

        Optional<RewardResponseDto> response = rewardService.getRewardsForCustomer(11L, request);
        assertEquals(0, response.get().getTotalPoints()); 
    }
    
    
    @Test
    void testMultipleTransactionsInSameMonth() {
        Customer customer = createCustomer(12L, "Aggregator", "agg@example.com");
        LocalDateTime now = LocalDateTime.now();

        Transaction t1 = createTransaction(12L, customer, BigDecimal.valueOf(120), now.minusDays(3));
        Transaction t2 = createTransaction(13L, customer, BigDecimal.valueOf(80), now.minusDays(2));

        Mockito.when(customerRepository.findById(12L)).thenReturn(Optional.of(customer));
        Mockito.when(transactionRepository.findByCustomer_IdAndTransactionDateBetween(
                Mockito.eq(12L), Mockito.any(), Mockito.any()))
                .thenReturn(Arrays.asList(t1, t2));

        RewardRequestDto request = new RewardRequestDto();
        request.setMonths(1);

        Optional<RewardResponseDto> response = rewardService.getRewardsForCustomer(12L, request);

        assertEquals(120, response.get().getTotalPoints());

        String monthKey = now.format(DateTimeFormatter.ofPattern("yyyy-MM"));
        assertEquals(120, response.get().getMonthlyPoints().get(monthKey));
    }


}
