package com.company.rewards.controller;

import com.company.rewards.dto.RewardRequestDTO;
import com.company.rewards.dto.RewardResponseDTO;
import com.company.rewards.service.RewardService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RewardController.class)
class RewardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RewardService rewardService;

    @Test
    void testMissingCustomerId() throws Exception {
        String requestJson = "{ \"months\": 3 }";

        mockMvc.perform(post("/api/rewards/customer/rewards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("CustomerId is required"));
    }

    @Test
    void testInvalidMonths() throws Exception {
        String requestJson = "{ \"customerId\": 21, \"months\": 0 }";

        mockMvc.perform(post("/api/rewards/customer/rewards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Months must be positive"));
    }

    @Test
    void testMissingFromOrTo() throws Exception {
        String requestJson = "{ \"customerId\": 21, \"from\": \"2026-01-01\" }";

        mockMvc.perform(post("/api/rewards/customer/rewards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Both 'from' and 'to' dates must be provided together"));
    }

    @Test
    void testCustomerNotFound() throws Exception {
        Mockito.when(rewardService.getRewardsForCustomer(21L, 3, null, null))
                .thenReturn(Optional.empty());

        String requestJson = "{ \"customerId\": 21, \"months\": 3 }";

        mockMvc.perform(post("/api/rewards/customer/rewards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Customer not found"));
    }

    @Test
    void testValidResponse() throws Exception {
        RewardResponseDTO mockResponse = new RewardResponseDTO(
                21L,
                "Alice",
                "alice@example.com",
                Collections.singletonMap("2026-03", 90),
                90,
                Collections.emptyList()
        );

        Mockito.when(rewardService.getRewardsForCustomer(21L, 3, null, null))
                .thenReturn(Optional.of(mockResponse));

        String requestJson = "{ \"customerId\": 21, \"months\": 3 }";

        mockMvc.perform(post("/api/rewards/customer/rewards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerId").value(21))
                .andExpect(jsonPath("$.customerName").value("Alice"))
                .andExpect(jsonPath("$.customerEmail").value("alice@example.com"))
                .andExpect(jsonPath("$.totalPoints").value(90));
    }
}