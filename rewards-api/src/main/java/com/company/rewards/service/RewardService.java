package com.company.rewards.service;

import com.company.rewards.dto.RewardRequestDto;
import com.company.rewards.dto.RewardResponseDto;
import java.time.LocalDate;
import java.util.Optional;

public interface RewardService {
	Optional<RewardResponseDto> getRewardsForCustomer(RewardRequestDto request);
}
