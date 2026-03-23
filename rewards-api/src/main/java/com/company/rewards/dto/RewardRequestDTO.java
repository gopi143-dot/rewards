package com.company.rewards.dto;

import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;

public class RewardRequestDTO {
    private Long customerId;
    private Integer months;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate from;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate to;

    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }
    public Integer getMonths() { return months; }
    public void setMonths(Integer months) { this.months = months; }
    public LocalDate getFrom() { return from; }
    public void setFrom(LocalDate from) { this.from = from; }
    public LocalDate getTo() { return to; }
    public void setTo(LocalDate to) { this.to = to; }
}