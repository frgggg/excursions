package com.excursions.excursions.client;

import feign.Headers;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@FeignClient(value = "UserClient", url = "${excursions-users.url}")
public interface UserClient {

    @PutMapping(value = "${excursions-users.api-coins-up-by-excursion}", produces = "application/json")
    @Headers("Content-Type: application/json")
    @ResponseBody
    ResponseEntity<String> coinsUpByExcursion(@PathVariable(name = "id")Long id, @RequestParam(name = "coins") Long coins);

    @PutMapping(value = "${excursions-users.api-coins-down-by-excursion}", produces = "application/json")
    @Headers("Content-Type: application/json")
    @ResponseBody
    ResponseEntity<String> coinsDownByExcursion(@PathVariable(name = "id")Long id, @RequestParam(name = "coins") Long coins);
}
