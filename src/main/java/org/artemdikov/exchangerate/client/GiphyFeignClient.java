package org.artemdikov.exchangerate.client;

import feign.Response;
import org.springframework.cloud.netflix.ribbon.RibbonClient;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name="giphy", url = "${giphy.service}")
@RibbonClient(name="giphy")
public interface GiphyFeignClient {
    @GetMapping("/v1/gifs/random?api_key=0Hp9hSWJq8ZMg2k7ezp2coxR6p7flcP0&tag={tag}&rating=g")
    public Response getGiphy(@PathVariable String tag);
}