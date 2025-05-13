package com.example.fraud_detection_application.service;
import com.example.fraud_detection_application.entity.Transaction;
import com.example.fraud_detection_application.repository.TransactionRepository;
import com.example.fraud_detection_application.utils.TransactionState;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class TransactionListener {
    private final TransactionRepository transactionRepository;
    private final FraudDetectionService fraudDetectionService;

    public TransactionListener(TransactionRepository transactionRepository, FraudDetectionService fraudDetectionService) {
        this.transactionRepository = transactionRepository;
        this.fraudDetectionService = fraudDetectionService;
    }

    @KafkaListener(topics = "transactions", groupId = "fraud-detector-group", containerFactory = "kafkaListenerContainerFactory")
    public void listen(Transaction request) {
        log.info("Received transaction: {}", request);

        request.setState(TransactionState.PENDING);
        transactionRepository.save(request);

        performFraudCheck(request);
    }

    private void performFraudCheck(Transaction request) {
        if(fraudDetectionService.isFraudulent(request)){
            request.setState(TransactionState.FLAGGED);
            transactionRepository.save(request);
        }
        else {
            request.setState(TransactionState.APPROVED);
            transactionRepository.save(request);
        }
    }
}

