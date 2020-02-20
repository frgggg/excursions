package com.excursions.excursions.service.impl;

import com.excursions.excursions.exception.ServiceException;
import com.excursions.excursions.repository.PlaceRepository;
import com.excursions.excursions.service.PlaceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.excursions.excursions.log.message.PlaceServiceLogMessages.PLACE_SERVICE_LOG_GET_NOT_EXIST_PLACES_IDS;

@Slf4j
@Service
public class PlaceServiceImpl implements PlaceService {

    private static final String SERVICE_NAME = "PlaceServiceImpl";

    private PlaceRepository placeRepository;

    @Autowired
    protected PlaceServiceImpl(PlaceRepository placeRepository) {
        this.placeRepository = placeRepository;
    }

    @Override
    public List<Long> getNotExistPlacesIds(List<Long> placesIdsForCheck) {
        List<Long> notExistPlacesIds;

        try {
           notExistPlacesIds = placeRepository.getNotExistPlacesIds(placesIdsForCheck);
        } catch (IllegalStateException e) {
            throw new ServiceException(SERVICE_NAME, e.getMessage());
        }

        log.debug(SERVICE_NAME, PLACE_SERVICE_LOG_GET_NOT_EXIST_PLACES_IDS);
        return notExistPlacesIds;
    }
}
