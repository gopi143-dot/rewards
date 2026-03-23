package com.company.rewards.controller;

import com.company.rewards.dto.RewardRequestDTO;
import com.company.rewards.dto.RewardResponseDTO;
import com.company.rewards.service.RewardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/rewards")
public class RewardController {
    private final RewardService rewardService;

    @Autowired
    public RewardController(RewardService rewardService) {
        this.rewardService = rewardService;
    }

    @PostMapping("/customer/rewards")
    public ResponseEntity<?> getRewardsForCustomer(@RequestBody RewardRequestDTO request) {
        if (request.getCustomerId() == null) {
            return ResponseEntity.badRequest().body("CustomerId is required");
        }
        if (request.getMonths() != null && request.getMonths() <= 0) {
            return ResponseEntity.badRequest().body("Months must be positive");
        }
        if ((request.getFrom() != null && request.getTo() == null) || (request.getFrom() == null && request.getTo() != null)) {
            return ResponseEntity.badRequest().body("Both 'from' and 'to' dates must be provided together");
        }
        Optional<RewardResponseDTO> response = rewardService.getRewardsForCustomer(
                request.getCustomerId(),
                request.getMonths(),
                request.getFrom(),
                request.getTo()
        );
        if (!response.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Customer not found");
        }
        return ResponseEntity.ok(response.get());
    }
}