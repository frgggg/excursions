package com.excursions.excursions.service;

import com.excursions.excursions.exception.ServiceException;
import com.excursions.excursions.model.Excursion;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

public interface ExcursionService {

    Excursion save(String name, LocalDateTime start, LocalDateTime stop, Integer peopleCount, Long coinsCost, List<Long> placesIds);
    void deleteEndedExcursions();
    void deleteNotEndedExcursionsByNotExistPlaces();
    Excursion findById(Long id);
    List<Excursion> findAll();

    void setEnabledNewTicketsById(Long id);
    void setNotEnabledNewTicketsById(Long id);
}
