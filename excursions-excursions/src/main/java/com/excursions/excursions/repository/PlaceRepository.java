package com.excursions.excursions.repository;

import java.util.List;

public interface PlaceRepository {
    List<Long> getNotExistPlacesIds(List<Long> placesIdsForCheck) throws IllegalStateException;
}
