package com.junive.customer;

import lombok.AllArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Service
@AllArgsConstructor
public class CustomerService {
    private final CustomerRepository customerRepository;
    private WebClient client;

    public Map registerCustomer(CustomerRequest request) {
        Customer customer = Customer.builder()
                .firstName(request.firstName())
                .lastName(request.lastName())
                .email(request.email())
                .build();
        customerRepository.saveAndFlush(customer);

        FraudCheckResponse fraudCheckResponse =
                client.get().uri("/check/{customerId}", customer.getId())
                .retrieve()
                .bodyToMono(FraudCheckResponse.class)
                .block();

        if (fraudCheckResponse.isFraudster()){
            throw new IllegalStateException("fraudster detected");
        }

        Map<Customer, FraudCheckResponse> hashMap = new HashMap<>();
        hashMap.put(customer, fraudCheckResponse);
        return hashMap;
    }
}
