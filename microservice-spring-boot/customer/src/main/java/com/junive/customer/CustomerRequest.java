package com.junive.customer;

public record CustomerRequest(
        String firstName,
        String lastName,
        String email) {
}