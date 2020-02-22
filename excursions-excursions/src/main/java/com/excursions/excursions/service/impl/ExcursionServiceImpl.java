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

import static com.excursions.excursions.exception.message.ExcursionServiceExceptionMessages.EXCURSION_SERVICE_EXCEPTION_NOT_EXIST_EXCURSION;
import static com.excursions.excursions.exception.message.ExcursionServiceExceptionMessages.EXCURSION_SERVICE_EXCEPTION_SAVE_OR_UPDATE_EXIST_PLACE;
import static com.excursions.excursions.log.message.ExcursionServiceLogMessages.*;

@Slf4j
@Service
public class ExcursionServiceImpl implements ExcursionService {

    private ExcursionRepository excursionRepository;
    private EntityManager entityManager;
    private PlaceService placeService;
    private TicketService ticketService = null;

    @Value("${excursion.ended.after-day}")
    private String deleteEndedExcursionsAfterDay;

    @Autowired
    protected ExcursionServiceImpl(ExcursionRepository excursionRepository, EntityManager entityManager, PlaceService placeService) {
        this.excursionRepository = excursionRepository;
        this.entityManager = entityManager;
        this.placeService = placeService;
    }

    @Transactional(isolation = Isolation.SERIALIZABLE, rollbackFor = ServiceException.class)
    @Override
    public Excursion save(String name, LocalDateTime start, LocalDateTime stop, Integer peopleCount, Long coinsCost, List<Long> placesIds) {
        Excursion excursionForSave = new Excursion(name, start, stop, peopleCount, coinsCost, placesIds);
        Excursion savedExcursion = saveUtil(excursionForSave);
        log.info(EXCURSION_SERVICE_LOG_NEW_EXCURSION, savedExcursion);
        return savedExcursion;
    }

    @Transactional(isolation = Isolation.SERIALIZABLE, rollbackFor = ServiceException.class)
    @Override
    public void setEnabledNewTicketsById(Long id) {
        Excursion excursionForUpdate = findById(id);
        if(!excursionForUpdate.getEnableNewTickets()) {
            excursionForUpdate.setEnableNewTickets(true);
            saveUtil(excursionForUpdate);
        }
        log.info(EXCURSION_SERVICE_LOG_SET_ENABLE_NEW_TICKETS, excursionForUpdate);
    }

    @Transactional(isolation = Isolation.SERIALIZABLE, rollbackFor = ServiceException.class)
    @Override
    public void setNotEnabledNewTicketsById(Long id) {
        Excursion excursionForUpdate = findById(id);
        if(excursionForUpdate.getEnableNewTickets()) {
            excursionForUpdate.setEnableNewTickets(false);
            saveUtil(excursionForUpdate);
        }
        log.info(EXCURSION_SERVICE_LOG_SET_NOT_ENABLE_NEW_TICKETS, excursionForUpdate);
    }

    @Transactional(isolation = Isolation.SERIALIZABLE, rollbackFor = ServiceException.class)
    @Override
    public void deleteEndedExcursions() {
        List<Excursion> endedExcursions = excursionRepository.findByStopBefore(LocalDateTime.now().plusDays(new Integer(deleteEndedExcursionsAfterDay)));
        if(endedExcursions != null) {
            if(endedExcursions.size() > 0) {
                ticketService.setActiveTicketsAsDropByEndedExcursions(endedExcursions);
                excursionRepository.deleteAll(endedExcursions);
            }
        }
        log.info(EXCURSION_SERVICE_LOG_DELETE_ENDED_EXCURSION, endedExcursions);
    }

    @Transactional(isolation = Isolation.SERIALIZABLE, rollbackFor = ServiceException.class)
    @Override
    public void deleteNotEndedExcursionsByNotExistPlaces() {
        List<Excursion> notEndedExcursionsWithNotExistPlaces = null;
        List<Long> notExistPlacesIds = null;

        List<Long> allPlacesIds = excursionRepository.getAllPlacesIds();

        if(allPlacesIds != null) {
            if(allPlacesIds.size() > 0) {
                notExistPlacesIds = placeService.getNotExistPlacesIds(allPlacesIds);
                if (notExistPlacesIds.size() > 0) {
                    notEndedExcursionsWithNotExistPlaces = excursionRepository.findByPlacesIdsInAndStartAfter(notExistPlacesIds, LocalDateTime.now());
                    if(notEndedExcursionsWithNotExistPlaces != null) {
                        if(notEndedExcursionsWithNotExistPlaces.size() > 0) {
                            ticketService.setActiveTicketsAsDropByWrongExcursions(notEndedExcursionsWithNotExistPlaces);
                            excursionRepository.deleteAll(notEndedExcursionsWithNotExistPlaces);
                        }
                    }
                }
            }
        }

        log.info(EXCURSION_SERVICE_LOG_DELETE_NOT_ENDED_EXCURSION_BY_NOT_EXIST_PLACE, notEndedExcursionsWithNotExistPlaces, notExistPlacesIds);
    }

    @Override
    public Excursion findById(Long id) {
        Optional<Excursion> optionalExcursion = excursionRepository.findById(id);
        if(!optionalExcursion.isPresent()) {
            throw new ServiceException(String.format(EXCURSION_SERVICE_EXCEPTION_NOT_EXIST_EXCURSION, id));
        }
        Excursion findByIdExcursion = optionalExcursion.get();
        log.info(EXCURSION_SERVICE_LOG_FIND_EXCURSION, findByIdExcursion);
        return findByIdExcursion;
    }

    @Override
    public List<Excursion> findAll() {
        List<Excursion> excursions = new ArrayList<>();
        excursionRepository.findAll().forEach(excursions::add);
        log.info(EXCURSION_SERVICE_LOG_FIND_ALL);
        return excursions;
    }

    private Excursion saveUtil(Excursion excursionForSave) {
        Excursion savedExcursion;
        try {
            savedExcursion = excursionRepository.save(excursionForSave);
            entityManager.flush();
        } catch (ConstraintViolationException e) {
            throw new ServiceException(e.getConstraintViolations().iterator().next().getMessage());
        } catch (PersistenceException e) {
            throw new ServiceException(EXCURSION_SERVICE_EXCEPTION_SAVE_OR_UPDATE_EXIST_PLACE);
        }

        return savedExcursion;
    }

    public void setTicketService(TicketService ticketService) {
        if(this.ticketService == null) {
            this.ticketService = ticketService;
        }
    }
}
