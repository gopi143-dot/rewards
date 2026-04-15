package com.charter.rewards.service;

import com.charter.rewards.dto.RewardRequestDto;
import com.charter.rewards.dto.RewardResponseDto;
import java.time.LocalDate;
import java.util.Optional;

public interface RewardService {
	Optional<RewardResponseDto> getRewardsForCustomer(Long customerId,RewardRequestDto request);
}
