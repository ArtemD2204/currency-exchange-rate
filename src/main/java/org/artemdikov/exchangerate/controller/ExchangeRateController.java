package org.artemdikov.exchangerate.controller;

import feign.Response;
import org.apache.commons.io.IOUtils;
import org.artemdikov.exchangerate.client.ExchangeRateFeignClient;
import org.artemdikov.exchangerate.client.GiphyFeignClient;
import org.artemdikov.exchangerate.model.ExchangeRate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

@RestController
public class ExchangeRateController {

    @Value("${exchangerate.base}")
    private String base;

    @Value("${tag.rich}")
    private String rich;

    @Value("${tag.broke}")
    private String broke;

    private ExchangeRateFeignClient exchangeRateFeignClient;

    private GiphyFeignClient giphyFeignClient;

    @Autowired
    public ExchangeRateController(ExchangeRateFeignClient exchangeRateFeignClient, GiphyFeignClient giphyFeignClient) {
        this.exchangeRateFeignClient = exchangeRateFeignClient;
        this.giphyFeignClient = giphyFeignClient;
    }

    @GetMapping(value = "/exchangerate/{currency}", produces = MediaType.IMAGE_GIF_VALUE)
    public ResponseEntity<byte[]> getExchangeRate(@PathVariable String currency) throws IOException {
        Calendar calendar = Calendar.getInstance();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        ExchangeRate todayData = exchangeRateFeignClient.getExchangeRate(dateFormat.format(calendar.getTime()));
        calendar.add(Calendar.DATE, -1);
        ExchangeRate yesterdayData = exchangeRateFeignClient.getExchangeRate(dateFormat.format(calendar.getTime()));
        BigDecimal todayRate = todayData.getRates().get(base).divide(todayData.getRates().get(currency), 7, RoundingMode.HALF_UP);
        BigDecimal yesterdayRate = yesterdayData.getRates().get(base).divide(yesterdayData.getRates().get(currency), 7, RoundingMode.HALF_UP);
        String tag = broke;
        if (todayRate.compareTo(yesterdayRate) > 0) {
            tag = rich;
        }
        String url = null;
        Response giphyResponse = giphyFeignClient.getGiphy(tag);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(giphyResponse.body().asInputStream()))) {
            String[] strArr = reader.readLine().split(",");
            int i = 0;
            for (; i < strArr.length; i++) {
                if (strArr[i].endsWith(".gif\"")) {
                    break;
                }
            }
            if (i < strArr.length) {
                strArr = strArr[i].trim().split("\\\\/");
                int j = 0;
                for (; j < strArr.length; j++) {
                    if (strArr[j].equals("media")) {
                        break;
                    }
                }
                if (j < strArr.length) {
                    url = "https://i.giphy.com/media/" + strArr[j+1] + "/giphy.webp";
                }
            }
        }
        InputStream in = new URL(url).openStream();
        byte[] gif = IOUtils.toByteArray(in);
        return new ResponseEntity<>(gif, HttpStatus.OK);
    }
}
