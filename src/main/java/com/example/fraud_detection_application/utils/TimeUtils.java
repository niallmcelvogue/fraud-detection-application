package com.example.fraud_detection_application.utils;

import lombok.experimental.UtilityClass;

import java.time.Duration;
import java.time.Instant;

@UtilityClass
public class TimeUtils {
    public static Double calculateTimeDifferenceInHours(Instant currentTransaction, Instant previousTransaction) {
            Duration duration = Duration.between(previousTransaction, currentTransaction);
            return duration.toMinutes() / 60.0;
        }
}
