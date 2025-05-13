package com.example.fraud_detection_application.service;

import com.example.fraud_detection_application.entity.Transaction;
import com.example.fraud_detection_application.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@SpringBootTest
public class FraudDetectionServiceTest {

    @MockBean
    private Clock fixedClock;

    @MockBean
    private TransactionRepository transactionRepository;

    @Autowired
    private FraudDetectionService fraudDetectionService;

    private final Instant fixedInstant = Instant.parse("2025-05-13T12:00:00Z");

    @BeforeEach
    void setUp() {
        when(fixedClock.instant()).thenReturn(fixedInstant);
        when(fixedClock.getZone()).thenReturn(ZoneId.of("Europe/London"));
    }

    @Test
    void testRapidTransactionRuleTriggered() {
        String userId = "user-1234";

        // Make sure that the query is using the right ZonedDateTime and the right userId
        when(transactionRepository.countByUserIdAndTimestampAfter(userId, fixedInstant.minus(5, ChronoUnit.MINUTES)))
                .thenReturn(Optional.of(6L));

        Transaction newTransaction = createTransaction(userId, fixedInstant);
        Boolean isFraudulent = fraudDetectionService.isFraudulent(newTransaction);

        assertTrue(isFraudulent);
    }


    private Transaction createTransaction(String userId, Instant timestamp) {
        Transaction transaction = new Transaction();
        transaction.setUserId(userId);
        transaction.setAmount(BigDecimal.valueOf(100));
        transaction.setTimestamp(timestamp);
        transaction.setLatitude(37.7749);  // Example latitude (San Francisco)
        transaction.setLongitude(-122.4194);  // Example longitude (San Francisco)
        return transaction;
    }

    @Test
    void testSuspiciousLocationRuleTriggered() {
        String userId = "user-1234";
        Instant now = Instant.now();

        // Transaction in London
        Transaction previousTransaction = new Transaction();
        previousTransaction.setId(UUID.randomUUID());
        previousTransaction.setUserId(userId);
        previousTransaction.setLatitude(51.5074);
        previousTransaction.setLongitude(-0.1278);
        previousTransaction.setAmount(BigDecimal.valueOf(100));
        previousTransaction.setTimestamp(now.minus(1, ChronoUnit.HOURS));

        // Transaction in San Francisco one hour later
        Transaction currentTransaction = new Transaction();
        currentTransaction.setId(UUID.randomUUID());
        currentTransaction.setUserId(userId);
        currentTransaction.setLatitude(37.7749);
        currentTransaction.setLongitude(-122.4194);
        currentTransaction.setAmount(BigDecimal.valueOf(200));
        currentTransaction.setTimestamp(now);

        when(transactionRepository.findSecondMostRecentByUserId(currentTransaction.getUserId()))
                .thenReturn(Optional.of(previousTransaction));

        Boolean isFraudulent = fraudDetectionService.isFraudulent(currentTransaction);

        assertTrue(isFraudulent, "Transaction should be flagged as fraudulent due to suspicious location travel");
        verify(transactionRepository, times(1)).findSecondMostRecentByUserId(currentTransaction.getUserId());
    }

    @Test
    void overLimitAmountRuleTriggered() {
        String userId = "user-1234";

        Transaction tx = new Transaction();
        tx.setId(UUID.randomUUID());
        tx.setUserId(userId);
        tx.setTimestamp(Instant.now());
        tx.setAmount(BigDecimal.valueOf(20_000));
        tx.setLatitude(37.7749);
        tx.setLongitude(-122.4194);

        Boolean isFraudulent = fraudDetectionService.isFraudulent(tx);

        assertTrue(isFraudulent, "Transaction should be flagged as over maximum amount limit");
    }
}
