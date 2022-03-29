package com.junive.fraud;

import java.time.LocalDateTime;

public record FraudCheckResponse(Boolean isFraudster, LocalDateTime createdAt) {
}
