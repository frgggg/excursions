package com.excursions.excursions.repository.impl;

import com.excursions.excursions.client.PlaceClient;
import com.excursions.excursions.repository.PlaceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class PlaceRepositoryImplPlaceClient implements PlaceRepository {

    private PlaceClient placeClient;

    @Autowired
    protected PlaceRepositoryImplPlaceClient(PlaceClient placeClient) {
        this.placeClient = placeClient;
    }

    @Override
    public List<Long> getNotExistPlacesIds(List<Long> placesIdsForCheck) {
        ResponseEntity<List<Long>> response;

        try {
            response = placeClient.getNotExistPlacesIds(placesIdsForCheck);
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage());
        }

        if(response.getStatusCode() != HttpStatus.OK) {
            throw new IllegalStateException(response.getBody().toString());
        }

        List<Long> notExistPlacesIds = response.getBody();
        if(notExistPlacesIds == null) {
            notExistPlacesIds = new ArrayList<>();
        }
        return notExistPlacesIds;
    }
}
