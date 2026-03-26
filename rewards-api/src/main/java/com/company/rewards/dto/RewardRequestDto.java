package com.company.rewards.dto;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
@Data
public class RewardRequestDto {

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
    @com.fasterxml.jackson.annotation.JsonIgnore
    public boolean isDateRangeValid() {
        return (from == null && to == null) || (from != null && to != null);
    }
    
    @AssertTrue(message = "'from' date must be before or equal to 'to' date")
    @com.fasterxml.jackson.annotation.JsonIgnore
    public boolean isFromBeforeTo() {
        if (from != null && to != null) {
            return !from.isAfter(to);
        }
        return true; // valid if either is null (handled by isDateRangeValid)
    }

    @AssertTrue(message = "Provide either 'months' or a date range, not both")
    @com.fasterxml.jackson.annotation.JsonIgnore
    public boolean isExclusiveChoice() {
        boolean hasMonths = months != null;
        boolean hasDateRange = (from != null && to != null);
        return !(hasMonths && hasDateRange);
    }

}