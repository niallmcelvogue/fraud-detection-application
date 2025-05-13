package com.example.fraud_detection_application.service;

import com.example.fraud_detection_application.entity.Transaction;
import com.example.fraud_detection_application.repository.TransactionRepository;
import com.example.fraud_detection_application.utils.GeoUtils;
import com.example.fraud_detection_application.utils.TimeUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static com.example.fraud_detection_application.utils.Constants.*;

@Service
public class FraudDetectionService {
    private final TransactionRepository transactionRepository;
    private final Clock clock;


    public FraudDetectionService(TransactionRepository transactionRepository, Clock clock) {
        this.transactionRepository = transactionRepository;
        this.clock = clock;
    }

    public Boolean isFraudulent(Transaction transaction) {
        return isOverAmountLimit(transaction.getAmount())
                || isLocationSuspicious(transaction)
                || isRapidTransaction(transaction);
    }

    private Boolean isOverAmountLimit(BigDecimal amount) {
        return amount.compareTo(BigDecimal.valueOf(MAX_AMOUNT_LIMIT)) > 0;
    }

    private Boolean isLocationSuspicious(Transaction transaction) {
        Optional<Transaction> optionalPreviousTransaction = transactionRepository.findSecondMostRecentByUserId(transaction.getUserId());

        if(optionalPreviousTransaction.isPresent()) {
            Transaction previousTransaction = optionalPreviousTransaction.get();
            Double distanceBetweenLocations = GeoUtils.haversine(transaction.getLatitude(), transaction.getLongitude(),
                    previousTransaction.getLatitude(), previousTransaction.getLongitude());

            Double timeBetweenTransactions = TimeUtils.calculateTimeDifferenceInHours(transaction.getTimestamp(), previousTransaction.getTimestamp());

            if(timeBetweenTransactions > 0) {
                double speed = distanceBetweenLocations / timeBetweenTransactions;
                return speed > MAX_SPEED_LIMIT;
            }
        }
        return false;
    }

    private Boolean isRapidTransaction(Transaction transaction) {
        Instant fiveMinutesAgo = Instant.now(clock).minus(5, ChronoUnit.MINUTES);
        Optional<Long> recentTransactionCount = transactionRepository.countByUserIdAndTimestampAfter(transaction.getUserId(), fiveMinutesAgo);
        if(recentTransactionCount.isPresent()) {
            return recentTransactionCount.get() > MAX_RAPID_TRANSACTION_VALUE;
        }
        return false;
    }
}