package com.junive.fraud;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("fraud")
public class FraudController {

    private final FraudCheckService fraudCheckService;

    @GetMapping(path = "check/{customerId}")
    public FraudCheckResponse isFraudster (  @PathVariable("customerId") Integer customerId) {
        FraudCheck fraudCheck = fraudCheckService.isFraudulentCustomer(customerId);
        return new FraudCheckResponse(fraudCheck.getIsFraudster(), fraudCheck.getCreatedAt());
    }
}
