package com.anisimovdenis.services;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "gif-client", url = "${gif.server.url}")
public interface GifServer {

    @GetMapping("/v1/gifs/random")
    String getGifObject(
            @RequestParam("api_key") String apiKey,
            @RequestParam("tag") String tag
    );
}
