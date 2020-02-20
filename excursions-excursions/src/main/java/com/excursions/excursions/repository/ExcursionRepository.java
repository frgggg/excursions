package com.excursions.excursions.repository;

import com.excursions.excursions.model.Excursion;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ExcursionRepository extends CrudRepository<Excursion, Long> {

    @Query(value = "select distinct elements(exc.placesIds) from Excursion exc")
    List<Long> getAllPlacesIds();

    List<Excursion> findByPlacesIdsInAndStartAfter(List<Long> notExistPlacesIds, LocalDateTime time);

    List<Excursion> findByStopBefore(LocalDateTime time);
}
