package com.junive.customer;

import java.time.LocalDateTime;

public record FraudCheckResponse(Boolean isFraudster, LocalDateTime createdAt) {
}
