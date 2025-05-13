package com.example.fraud_detection_application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TransactionRequest {
    private BigDecimal amount;
    private String userId;
    private String merchantId;
    private double latitude;
    private double longitude;
}
