package com.company.rewards.controller;

import com.company.rewards.dto.RewardResponseDto;
import com.company.rewards.service.RewardService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RewardController.class)
class RewardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RewardService rewardService;

    @Test
    void testInvalidCustomerId() throws Exception {
        mockMvc.perform(get("/api/rewards/customer/0")
                        .param("months", "3"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testInvalidMonths() throws Exception {
        mockMvc.perform(get("/api/rewards/customer/21")
                        .param("months", "0"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testMissingFromOrTo() throws Exception {
        mockMvc.perform(get("/api/rewards/customer/21")
                        .param("from", "2026-01-01"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCustomerNotFound() throws Exception {
        Mockito.when(rewardService.getRewardsForCustomer(Mockito.eq(21L), Mockito.any()))
                .thenReturn(Optional.empty());

        mockMvc.perform(get("/api/rewards/customer/21")
                        .param("months", "3"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode").value("404 NOT_FOUND"))
                .andExpect(jsonPath("$.errorMessage").value("Customer not found"));
    }

    @Test
    void testValidResponse() throws Exception {
        RewardResponseDto mockResponse = new RewardResponseDto(
                21L,
                "Alice",
                "alice@example.com",
                Collections.singletonMap("2026-03", 90),
                90,
                Collections.emptyList()
        );

        Mockito.when(rewardService.getRewardsForCustomer(Mockito.eq(21L), Mockito.any()))
                .thenReturn(Optional.of(mockResponse));

        mockMvc.perform(get("/api/rewards/customer/21")
                        .param("months", "3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerId").value(21))
                .andExpect(jsonPath("$.customerName").value("Alice"))
                .andExpect(jsonPath("$.customerEmail").value("alice@example.com"))
                .andExpect(jsonPath("$.totalPoints").value(90));
    }

    @Test
    void testValidResponseWithDateRange() throws Exception {
        RewardResponseDto mockResponse = new RewardResponseDto(
                21L,
                "Bob",
                "bob@example.com",
                Collections.singletonMap("2026-03", 50),
                50,
                Collections.emptyList()
        );

        Mockito.when(rewardService.getRewardsForCustomer(Mockito.eq(21L), Mockito.any()))
                .thenReturn(Optional.of(mockResponse));

        mockMvc.perform(get("/api/rewards/customer/21")
                        .param("from", "2026-03-01")
                        .param("to", "2026-03-20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerId").value(21))
                .andExpect(jsonPath("$.customerName").value("Bob"))
                .andExpect(jsonPath("$.customerEmail").value("bob@example.com"))
                .andExpect(jsonPath("$.totalPoints").value(50));
    }
}