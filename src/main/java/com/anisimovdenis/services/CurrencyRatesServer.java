package com.anisimovdenis.services;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "currency-server", url = "${currency.rate.server.url}")
public interface CurrencyRatesServer {

    @GetMapping("/api/latest.json")
    String getLatestRates(
            @RequestParam("app_id") String appId,
            @RequestParam("base") String base,
            @RequestParam("symbols") String symbols
    );

    @GetMapping("/api/historical/{date}")
    String getHistoricalRates(
            @PathVariable("date") String date,
            @RequestParam("app_id") String appId,
            @RequestParam("base") String base,
            @RequestParam("symbols") String symbols
    );
}
