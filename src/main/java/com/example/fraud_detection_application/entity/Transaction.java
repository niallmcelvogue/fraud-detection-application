package com.example.fraud_detection_application.entity;

import com.example.fraud_detection_application.utils.TransactionState;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name="transactions")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Transaction {
    @GeneratedValue(strategy = GenerationType.UUID)
    @Id
    private UUID id;
    private BigDecimal amount;
    private Instant timestamp;
    private String userId;
    private String merchantId;
    private double latitude;
    private double longitude;
    @Enumerated(EnumType.STRING)
    private TransactionState state;
}
