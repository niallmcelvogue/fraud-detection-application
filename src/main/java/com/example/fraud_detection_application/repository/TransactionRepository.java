package com.example.fraud_detection_application.repository;

import com.example.fraud_detection_application.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, UUID> {
    @Query(value = "SELECT COUNT(t) FROM Transaction t WHERE t.userId = :userId AND t.timestamp > :timestamp")
    Optional<Long> countByUserIdAndTimestampAfter(@Param("userId") String userId, @Param("timestamp") Instant timestamp);

    @Query(value = "SELECT * FROM transactions WHERE user_id = :userId ORDER BY timestamp DESC LIMIT 1 OFFSET 1", nativeQuery = true)
    Optional<Transaction> findSecondMostRecentByUserId(@Param("userId") String userId);
}
