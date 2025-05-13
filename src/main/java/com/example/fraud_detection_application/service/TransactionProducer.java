package com.example.fraud_detection_application.service;

import com.example.fraud_detection_application.entity.Transaction;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class TransactionProducer {
    private static final String TOPIC = "transactions";

    private final KafkaTemplate<String, Transaction> kafkaTemplate;

    public TransactionProducer(KafkaTemplate<String, Transaction> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendTransaction(Transaction request) {
        kafkaTemplate.send(TOPIC, request.getUserId(), request);
    }
}
