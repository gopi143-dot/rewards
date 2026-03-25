package com.company.rewards.dto;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
@Data
public class RewardRequestDto {

    @NotNull(message = "Customer ID must not be null")
    private Long customerId;

    @Min(value = 1, message = "Months must be at least 1")
    private Integer months;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate from;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate to;

    /**
     * Custom validation: both 'from' and 'to' must be provided together.
     */
    @AssertTrue(message = "Both 'from' and 'to' dates must be provided together")
    public boolean isDateRangeValid() {
        return (from == null && to == null) || (from != null && to != null);
    }

}