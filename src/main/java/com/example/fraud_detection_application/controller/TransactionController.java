package com.example.fraud_detection_application.controller;

import com.example.fraud_detection_application.dto.TransactionRequest;
import com.example.fraud_detection_application.entity.Transaction;
import com.example.fraud_detection_application.repository.TransactionRepository;
import com.example.fraud_detection_application.service.TransactionProducer;
import com.example.fraud_detection_application.utils.TransactionState;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Clock;
import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {
    private final TransactionProducer transactionProducer;
    private final TransactionRepository transactionRepository;
    private final Clock clock;

    public TransactionController(TransactionProducer transactionProducer, TransactionRepository transactionRepository, Clock clock) {
        this.transactionProducer = transactionProducer;
        this.transactionRepository = transactionRepository;
        this.clock = clock;
    }

    @GetMapping
    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    @PostMapping
    public ResponseEntity<String> createTransaction(@RequestBody TransactionRequest request) {
        Transaction transaction = new Transaction();
        transaction.setAmount(request.getAmount());
        transaction.setUserId(request.getUserId());
        transaction.setMerchantId(request.getMerchantId());
        transaction.setTimestamp(Instant.now(clock));
        transaction.setLatitude(request.getLatitude());
        transaction.setLongitude(request.getLongitude());
        transaction.setState(TransactionState.RECEIVED);
        Transaction savedTransaction = transactionRepository.save(transaction);

        transactionProducer.sendTransaction(savedTransaction);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
