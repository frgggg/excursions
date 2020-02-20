package com.excursions.excursions.service;

import java.util.List;

public interface PlaceService {
    List<Long> getNotExistPlacesIds(List<Long> placesIdsForCheck);
}
