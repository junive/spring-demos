package com.junive.account.util;

public enum CustomStatus {
    // Not found
    USERNAME_PASSWORD_NOT_FOUND, ROLE_NOT_FOUND, HEADER_AUTHORIZATION_NOT_FOUND,
    // Invalid
    TOKEN_START_INVALID,
    // Conflict
    USERNAME_EXIST, EMAIL_EXIST,
    // Filter
    TOKEN_EXPIRED, NOT_VALID_TOKEN;
}
