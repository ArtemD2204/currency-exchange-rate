package org.artemdikov.exchangerate.client;

import org.artemdikov.exchangerate.model.ExchangeRate;
import org.springframework.cloud.netflix.ribbon.RibbonClient;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name="open-exchange-rates", url = "${exchangerate.service}")
@RibbonClient(name="open-exchange-rates")
public interface ExchangeRateFeignClient {
    @GetMapping("/api/historical/{date}.json?app_id=2c60f4db8b52413e8a59b7900e4740c5")
    public ExchangeRate getExchangeRate(@PathVariable String date);
}
