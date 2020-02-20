package com.excursions.excursions.client;

import feign.Headers;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(value = "PlaceClient", url = "${excursions-places.url}")
public interface PlaceClient {

    @GetMapping(value = "${excursions-places.api-check}", produces = "application/json")
    @Headers("Content-Type: application/json")
    @ResponseBody
    ResponseEntity<List<Long>> getNotExistPlacesIds(@RequestParam(name = "places-ids-for-check") List<Long> placesIdsForCheck);
}
