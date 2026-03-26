package com.company.rewards.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.company.rewards.dto.ErrorResponseDto;
import com.company.rewards.dto.RewardRequestDto;
import com.company.rewards.dto.RewardResponseDto;
import com.company.rewards.service.RewardService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@RestController
@RequestMapping("/api/rewards")
@Validated
public class RewardController {

    private final RewardService rewardService;

    public RewardController(RewardService rewardService) {
        this.rewardService = rewardService;
    }

    /**
     * GET endpoint that binds query parameters into RewardRequestDto.
     * Example:
     *   /api/rewards/customer?customerId=1&months=3
     *   /api/rewards/customer?customerId=1&from=2026-03-01&to=2026-03-20
     */
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<?> getRewardsForCustomer(
            @PathVariable @NotNull @Min(1) Long customerId,   // must not be null or zero
            @Valid RewardRequestDto request) {

        Optional<RewardResponseDto> response = rewardService.getRewardsForCustomer(customerId,request);

        if (response.isEmpty()) {
            ErrorResponseDto error = new ErrorResponseDto(
                    HttpStatus.NOT_FOUND.toString(),
                    "Customer not found"
            );
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }

        return ResponseEntity.ok(response.get());
    }

}