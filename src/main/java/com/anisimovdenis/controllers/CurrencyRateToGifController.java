package com.anisimovdenis.controllers;

import com.anisimovdenis.services.CurrencyRatesServer;
import com.anisimovdenis.services.GifServer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDate;

@Controller
@RequestMapping("/check")
public class CurrencyRateToGifController {

    private static final String INCREASE_RATE_TAG = "rich";
    private static final String DECREASE_RATE_TAG = "broke";
    private static final String JSON_SUFFIX = ".json";
    private static final String PARSE_GIF_OBJECT_NAME = "data";
    private static final String PARSE_GIF_URL_NAME = "url";
    private static final String PARSE_RATE_OBJECT_NAME = "rates";

    private final CurrencyRatesServer currencyRatesServer;
    private final GifServer gifServer;
    private final ObjectMapper jsonParser;

    @Value("${currency.rate.server.app_id}")
    private String converterAppId;

    @Value("${base.currency}")
    private String baseCurrency;

    @Value("${gif.server.api_key}")
    private String gifServerApiKey;

    @Autowired
    public CurrencyRateToGifController(CurrencyRatesServer currencyRatesServer, GifServer gifServer) {
        this.currencyRatesServer = currencyRatesServer;
        this.gifServer = gifServer;
        this.jsonParser = new ObjectMapper();
    }

    @GetMapping(value = "/{currency}")
    public String checkCurrency(@PathVariable String currency) {
        currency = currency.toUpperCase();
        baseCurrency = baseCurrency.toUpperCase();
        final LocalDate yesterday = LocalDate.now().minusDays(1);
        final String yesterdayResponse = currencyRatesServer.getHistoricalRates(yesterday + JSON_SUFFIX, converterAppId, baseCurrency, currency);
        final String latestResponse = currencyRatesServer.getLatestRates(converterAppId, baseCurrency, currency);
        final Double yesterdayRate = parseCurrencyServerResponse(yesterdayResponse, currency);
        final Double latestRate = parseCurrencyServerResponse(latestResponse, currency);
        if (latestRate == null || yesterdayRate == null) {
            return "error_page";
        }
        final String gifServerResponse = gifServer.getGifObject(gifServerApiKey, latestRate > yesterdayRate ? INCREASE_RATE_TAG : DECREASE_RATE_TAG);
        final String redirectUrl = parseGifServerResponse(gifServerResponse);
        if (redirectUrl == null) {
            return "error_page";
        }
        return "redirect:" + redirectUrl;
    }

    private String parseGifServerResponse(String jsonResponse) {
        try {
            return jsonParser.readTree(jsonResponse).get(PARSE_GIF_OBJECT_NAME).get(PARSE_GIF_URL_NAME).asText();
        } catch (JsonProcessingException | NullPointerException e) {
            return null;
        }
    }

    private Double parseCurrencyServerResponse(String jsonResponse, String currency) {
        try {
            return jsonParser.readTree(jsonResponse).get(PARSE_RATE_OBJECT_NAME).get(currency).asDouble();
        } catch (JsonProcessingException | NullPointerException e) {
            return null;
        }
    }
}
