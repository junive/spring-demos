package com.junive.customer;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Configuration
public class CustomerConfig {

    @Bean
    CommandLineRunner commandLineRunner(CustomerRepository repository) {
        return args -> {
            repository.deleteAll();

            Customer emp1 = new Customer(1, "Chris", "Malone", "chris@gmail.com");
            Customer emp2 = new Customer(2, "Alex", "Stone", "alex@gmail.com");
            Customer emp3 = new Customer(3, "John", "Doe", "doe@gmail.com");

            repository.saveAll(List.of(emp1, emp2, emp3));

        };
    }

    @Bean
    public WebClient client() {
        return WebClient.builder()
                .baseUrl("http://localhost:8081/fraud")
                .build();
    }


}
