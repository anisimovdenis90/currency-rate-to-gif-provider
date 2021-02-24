package com.anisimovdenis;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class CurrencyRateToGifProviderApplication {

    public static void main(String[] args) {
        SpringApplication.run(CurrencyRateToGifProviderApplication.class, args);
    }

}
