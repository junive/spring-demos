package com.junive.fraud;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class FraudCheckService {

    private final FraudCheckRepository fraudCheckHistoryRepository;

    public FraudCheck isFraudulentCustomer(Integer customerId) {
        FraudCheck fraudCheck = fraudCheckHistoryRepository.save(
                FraudCheck.builder()
                        .customerId(customerId)
                        .isFraudster(false)
                        .createdAt(LocalDateTime.now())
                        .build()
        );
        return fraudCheck;
    }

}