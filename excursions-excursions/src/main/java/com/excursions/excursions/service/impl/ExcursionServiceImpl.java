package com.excursions.excursions.service.impl;

import com.excursions.excursions.exception.ServiceException;
import com.excursions.excursions.model.Excursion;
import com.excursions.excursions.repository.ExcursionRepository;
import com.excursions.excursions.service.ExcursionService;
import com.excursions.excursions.service.PlaceService;
import com.excursions.excursions.service.TicketService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static com.excursions.excursions.exception.message.ExcursionServiceExceptionMessages.EXCURSION_SERVICE_EXCEPTION_NOT_EXIST_EXCURSION;
import static com.excursions.excursions.exception.message.ExcursionServiceExceptionMessages.EXCURSION_SERVICE_EXCEPTION_SAVE_OR_UPDATE_EXIST_PLACE;
import static com.excursions.excursions.log.message.ExcursionServiceLogMessages.*;
import static com.excursions.excursions.service.impl.util.ServicesUtil.isListNotNullNotEmpty;

@Slf4j
@Service
public class ExcursionServiceImpl implements ExcursionService {

    private ExcursionRepository excursionRepository;
    private PlaceService placeService;
    private TicketService ticketService = null;

    @Value("${excursion.ended.after-day}")
    private String deleteEndedExcursionsAfterDay;

    @Autowired
    protected ExcursionServiceImpl(ExcursionRepository excursionRepository, PlaceService placeService) {
        this.excursionRepository = excursionRepository;
        this.placeService = placeService;
    }

    @Override
    public Excursion save(String name, LocalDateTime start, LocalDateTime stop, Integer peopleCount, Long coinsCost, List<Long> placesIds) {
        Excursion excursionForSave = new Excursion(name, start, stop, peopleCount, coinsCost, placesIds);
        Excursion savedExcursion = saveOrUpdateUtil(excursionForSave);
        log.info(EXCURSION_SERVICE_LOG_NEW_EXCURSION, savedExcursion);
        return savedExcursion;
    }

    @Override
    public void setEnabledNewTicketsById(Long id) {
        Excursion excursionForUpdate = findById(id);
        if(!excursionForUpdate.getEnableNewTickets()) {
            excursionForUpdate.setEnableNewTickets(true);
            saveOrUpdateUtil(excursionForUpdate);
        }
        log.info(EXCURSION_SERVICE_LOG_SET_ENABLE_NEW_TICKETS, excursionForUpdate);
    }

    @Override
    public void setNotEnabledNewTicketsById(Long id) {
        Excursion excursionForUpdate = findById(id);
        if(excursionForUpdate.getEnableNewTickets()) {
            excursionForUpdate.setEnableNewTickets(false);
            saveOrUpdateUtil(excursionForUpdate);
        }
        log.info(EXCURSION_SERVICE_LOG_SET_NOT_ENABLE_NEW_TICKETS, excursionForUpdate);
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    @Override
    public void deleteEndedExcursions() {
        List<Excursion> endedExcursions = excursionRepository.findByStopBefore(
                LocalDateTime.now().minusDays(Integer.parseInt(deleteEndedExcursionsAfterDay))
        );

        if(isListNotNullNotEmpty(endedExcursions)) {
            ticketService.setActiveTicketsAsDropByEndedExcursions(endedExcursions);
            excursionRepository.deleteAll(endedExcursions);
        }

        log.info(EXCURSION_SERVICE_LOG_DELETE_ENDED_EXCURSION, endedExcursions);
    }

    @Override
    public void deleteNotEndedExcursionsByNotExistPlaces() {
        List<Long> allPlacesIds = excursionRepository.getAllPlacesIds();
        if(!isListNotNullNotEmpty(allPlacesIds))
            return;
        List<Long> notExistPlacesIds = placeService.getNotExistPlacesIds(allPlacesIds);
        if(!isListNotNullNotEmpty(notExistPlacesIds))
            return;
        List<Excursion> notEndedExcursionsWithNotExistPlaces = excursionRepository.findByPlacesIdsInAndStartAfter(notExistPlacesIds, LocalDateTime.now());
        if(!isListNotNullNotEmpty(notEndedExcursionsWithNotExistPlaces))
            return;

        try {
            ticketService.setActiveTicketsAsDropByWrongExcursions(notEndedExcursionsWithNotExistPlaces);
        } finally {
            excursionRepository.deleteAll(notEndedExcursionsWithNotExistPlaces);
        }

        log.info(EXCURSION_SERVICE_LOG_DELETE_NOT_ENDED_EXCURSION_BY_NOT_EXIST_PLACE, notEndedExcursionsWithNotExistPlaces, notExistPlacesIds);
    }

    @Override
    public Excursion findById(Long id) {
        Excursion excursion = excursionRepository.findById(id)
                .orElseThrow(
                        () -> new ServiceException(
                                String.format(EXCURSION_SERVICE_EXCEPTION_NOT_EXIST_EXCURSION, id))
                );

        log.info(EXCURSION_SERVICE_LOG_FIND_EXCURSION, excursion);
        return excursion;
    }

    @Override
    public List<Excursion> findAll() {
        log.info(EXCURSION_SERVICE_LOG_FIND_ALL);
        return StreamSupport.stream(excursionRepository.findAll().spliterator(), false)
                .collect(Collectors.toList());
    }

    private Excursion saveOrUpdateUtil(Excursion excursionForSave) {
        Excursion savedExcursion;
        try {
            savedExcursion = excursionRepository.save(excursionForSave);
        } catch (ConstraintViolationException e) {
            throw new ServiceException(e.getConstraintViolations().iterator().next().getMessage());
        } catch (DataIntegrityViolationException e) {
            throw new ServiceException(EXCURSION_SERVICE_EXCEPTION_SAVE_OR_UPDATE_EXIST_PLACE);
        } catch (Exception e) {
            throw new ServiceException(e.getMessage());
        }
        return savedExcursion;
    }

    public void setTicketService(TicketService ticketService) {
        if(this.ticketService == null) {
            this.ticketService = ticketService;
        }
    }
}
