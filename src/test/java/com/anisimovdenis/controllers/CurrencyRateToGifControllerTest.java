package com.anisimovdenis.controllers;

import com.anisimovdenis.services.CurrencyRatesServer;
import com.anisimovdenis.services.GifServer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlTemplate;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class CurrencyRateToGifControllerTest {

    @Value("${currency.rate.server.app_id}")
    private String converterAppId;

    @Value("${base.currency}")
    private String baseCurrency;

    @Value("${gif.server.api_key}")
    private String gifServerApiKey;

    @Autowired
    private MockMvc mvc;

    @MockBean
    private CurrencyRatesServer currencyRatesServer;

    @MockBean
    private GifServer gifServer;

    @Test
    public void checkCurrencyTest() throws Exception {
        String checkedCurrency = "EUR";
        String yesterdayResponse = "{\"rates\": {\"" + checkedCurrency + "\": 0.821085}}";
        String latestResponse = "{\"rates\": {\"" + checkedCurrency + "\": 0.822085}}";
        String tag = "rich";
        String redirectUrl = "https://giphy.com/gifs/batman-joker-BsZ8JyNQ1Ji9y";
        String gifResponse = "{\"data\": {\"url\": \"" + redirectUrl + "\"}}";
        given(currencyRatesServer.getLatestRates(
                converterAppId,
                baseCurrency,
                checkedCurrency
        )).willReturn(latestResponse);
        given(currencyRatesServer.getHistoricalRates(
                LocalDate.now().minusDays(1) + ".json",
                converterAppId,
                baseCurrency,
                checkedCurrency
        )).willReturn(yesterdayResponse);
        given(gifServer.getGifObject(gifServerApiKey, tag)).willReturn(gifResponse);

        mvc.perform(get("/check/" + checkedCurrency)
                .contentType(MediaType.TEXT_HTML))
                .andExpect(status().isFound())
                .andExpect(redirectedUrlTemplate(redirectUrl));
    }
}
