package org.artemdikov.exchangerate.controller;

import feign.Feign;
import feign.Request;
import feign.RequestTemplate;
import feign.Response;
import org.apache.commons.io.IOUtils;
import org.artemdikov.exchangerate.client.ExchangeRateFeignClient;
import org.artemdikov.exchangerate.client.GiphyFeignClient;
import org.artemdikov.exchangerate.model.ExchangeRate;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import static org.mockito.Mockito.*;

@SpringBootTest
@AutoConfigureMockMvc
public class ExchangeRateControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ExchangeRateFeignClient exchangeRateFeignClient;

    @MockBean
    private GiphyFeignClient giphyFeignClient;

    @Test
    public void getRichGifTest() throws Exception {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String today = dateFormat.format(new Date());
        String yesterday = dateFormat.format(new Date().getTime() - 3600L * 24 * 1000);
        HashMap<String, BigDecimal> todayRates = new HashMap<>();
        todayRates.put("RUB", new BigDecimal("100.00"));
        todayRates.put("USD", new BigDecimal("1.00"));
        ExchangeRate todayData = new ExchangeRate(null, null, null, null, todayRates);
        HashMap<String, BigDecimal> yesterdayRates = new HashMap<>();
        yesterdayRates.put("RUB", new BigDecimal("50.00"));
        yesterdayRates.put("USD", new BigDecimal("1.00"));
        ExchangeRate yesterdayData = new ExchangeRate(null, null, null, null, yesterdayRates);
        String richGifFromGiphyFeignClient = "{\"height\": \"270\"," +
                "               \"size\": \"5502308\"," +
                "               \"url\": \"https:\\/\\/media0.giphy.com\\/media\\/qDgVlx6szNGAdYyTuX\\/giphy-downsized-large.gif?cid=e168285cc0b7e38d08714ce393c6229ed25c52ea3cf2f2aa&rid=giphy-downsized-large.gif\"," +
                "               \"width\": \"480\"" +
                "           }";
        Response feignGifResponse = Response.builder()
                .request(Request.create(Request.HttpMethod.GET, "", new HashMap<>(),
                        new byte[0], Charset.defaultCharset(), new RequestTemplate()))
                .body(richGifFromGiphyFeignClient, StandardCharsets.UTF_8)
                .status(200)
                .headers(new HashMap<>())
                .build();
        byte[] resultGif;
        try(InputStream in = new URL("https://i.giphy.com/media/qDgVlx6szNGAdYyTuX/giphy.webp").openStream()) {
            resultGif = IOUtils.toByteArray(in);
        }
        when(exchangeRateFeignClient.getExchangeRate(today)).thenReturn(todayData);
        when(exchangeRateFeignClient.getExchangeRate(yesterday)).thenReturn(yesterdayData);
        when(giphyFeignClient.getGiphy("rich")).thenReturn(feignGifResponse);
        this.mockMvc.perform(MockMvcRequestBuilders.get("/exchangerate/USD"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().bytes(resultGif));
    }

    @Test
    public void getBrokeGifTest() throws Exception {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String today = dateFormat.format(new Date());
        String yesterday = dateFormat.format(new Date().getTime() - 3600L * 24 * 1000);
        HashMap<String, BigDecimal> todayRates = new HashMap<>();
        todayRates.put("RUB", new BigDecimal("50.00"));
        todayRates.put("USD", new BigDecimal("1.00"));
        ExchangeRate todayData = new ExchangeRate(null, null, null, null, todayRates);
        HashMap<String, BigDecimal> yesterdayRates = new HashMap<>();
        yesterdayRates.put("RUB", new BigDecimal("100.00"));
        yesterdayRates.put("USD", new BigDecimal("1.00"));
        ExchangeRate yesterdayData = new ExchangeRate(null, null, null, null, yesterdayRates);
        String brokeGifFromGiphyFeignClient = "{\"height\": \"270\"," +
                "               \"size\": \"5502308\"," +
                "               \"url\": \"https:\\/\\/media4.giphy.com\\/media\\/xUA7b3txo3USsXgDG8\\/giphy.gif?cid=e168285c5b7de6519ed097630c928707b6bde20af538886e&rid=giphy.gif\"," +
                "               \"width\": \"480\"" +
                "           }";
        Response feignGifResponse = Response.builder()
                .request(Request.create(Request.HttpMethod.GET, "", new HashMap<>(),
                        new byte[0], Charset.defaultCharset(), new RequestTemplate()))
                .body(brokeGifFromGiphyFeignClient, StandardCharsets.UTF_8)
                .status(200)
                .headers(new HashMap<>())
                .build();
        byte[] resultGif;
        try(InputStream in = new URL("https://i.giphy.com/media/xUA7b3txo3USsXgDG8/giphy.webp").openStream()) {
            resultGif = IOUtils.toByteArray(in);
        }
        when(exchangeRateFeignClient.getExchangeRate(today)).thenReturn(todayData);
        when(exchangeRateFeignClient.getExchangeRate(yesterday)).thenReturn(yesterdayData);
        when(giphyFeignClient.getGiphy("broke")).thenReturn(feignGifResponse);
        this.mockMvc.perform(MockMvcRequestBuilders.get("/exchangerate/USD"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().bytes(resultGif));
    }
}
