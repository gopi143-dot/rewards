package com.company.rewards.service;

import com.company.rewards.dto.RewardResponseDTO;
import java.time.LocalDate;
import java.util.Optional;

public interface RewardService {
    Optional<RewardResponseDTO> getRewardsForCustomer(Long customerId, Integer months, LocalDate from, LocalDate to);
}
