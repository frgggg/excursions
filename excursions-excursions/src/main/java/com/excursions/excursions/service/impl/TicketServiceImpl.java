package com.excursions.excursions.service.impl;

import com.excursions.excursions.exception.ServiceException;
import com.excursions.excursions.model.Excursion;
import com.excursions.excursions.model.Ticket;
import com.excursions.excursions.model.TicketState;
import com.excursions.excursions.repository.TicketRepository;
import com.excursions.excursions.service.ExcursionService;
import com.excursions.excursions.service.TicketService;
import com.excursions.excursions.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.validation.ConstraintViolationException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.excursions.excursions.exception.message.TicketServiceExceptionMessages.*;
import static com.excursions.excursions.log.message.TicketServiceLogMessages.*;

@Slf4j
@Service
public class TicketServiceImpl implements TicketService {

    private static final String SERVICE_NAME = "TicketServiceImpl";

    @Value("${ticket.drop-by-user-before-stop.day}")
    private String deleteByUserBeforeStartMinusDay;

    private EntityManager entityManager;
    private TicketRepository ticketRepository;
    private ExcursionService excursionService;
    private UserService userService;

    @Autowired
    protected TicketServiceImpl(TicketRepository ticketRepository, EntityManager entityManager, UserService userService) {
        this.ticketRepository = ticketRepository;
        this.entityManager = entityManager;
        this.userService = userService;
    }

    @Transactional(isolation = Isolation.SERIALIZABLE, rollbackFor = ServiceException.class)
    @Override
    public Ticket create(Long userId, Long excursionId, Long expectedCoinsCost) {
        Excursion excursion = excursionService.findById(excursionId);

        checkExcursionForStart(excursion);

        if(!expectedCoinsCost.equals(excursion.getCoinsCost())) {
            throw new ServiceException(SERVICE_NAME, TICKET_SERVICE_EXCEPTION_WRONG_COST);
        }

        if(!excursion.getEnableNewTickets()) {
            throw new ServiceException(SERVICE_NAME, TICKET_SERVICE_EXCEPTION_NEW_TICKET_NOT_ENABLE);
        }

        if(ticketRepository.countByExcursionIdAndState(excursionId, TicketState.ACTIVE) >= excursion.getPeopleCount()) {
            throw new ServiceException(SERVICE_NAME, TICKET_SERVICE_EXCEPTION_MAX_PEOPLE_COUNT);
        }

        Ticket ticketForSave = new Ticket(excursionId, expectedCoinsCost, userId);
        Ticket savedTicket = saveUtil(ticketForSave);
        userService.coinsDownByExcursion(userId, expectedCoinsCost);
        log.info(TICKET_SERVICE_LOG_NEW_TICKET, savedTicket);
        return savedTicket;
    }

    @Override
    public List<Ticket> findAll() {
        List<Ticket> tickets = new ArrayList<>();
        ticketRepository.findAll().forEach(tickets::add);
        log.info(TICKET_SERVICE_LOG_FIND_ALL);
        return tickets;
    }

    @Override
    public Ticket findById(Long id) {
        Optional<Ticket> optionalTicket = ticketRepository.findById(id);
        if(!optionalTicket.isPresent()) {
            throw new ServiceException(SERVICE_NAME, String.format(TICKET_SERVICE_EXCEPTION_NOT_EXIST_EXCURSION, id));
        }
        Ticket findByIdTicket = optionalTicket.get();
        log.info(TICKET_SERVICE_LOG_FIND_EXCURSION, findByIdTicket);
        return findByIdTicket;
    }

    @Transactional(isolation = Isolation.SERIALIZABLE, rollbackFor = ServiceException.class)
    @Override
    public void setActiveTicketsAsDropByUser(Long id) {
        Ticket ticket = findById(id);
        if(!TicketState.ACTIVE.equals(ticket.getState())) {
            throw new ServiceException(SERVICE_NAME, TICKET_SERVICE_EXCEPTION_TICKET_IS_NOT_ACTIVE);
        }

        Excursion excursion = excursionService.findById(ticket.getExcursionId());

        if(LocalDateTime.now().plusDays(new Integer(deleteByUserBeforeStartMinusDay)).isAfter(excursion.getStart())) {
            throw new ServiceException(SERVICE_NAME, TICKET_SERVICE_EXCEPTION_EXCURSION_STARTED);
        }

        ticket.setState(TicketState.DROP_BY_USER);
        saveUtil(ticket);
        log.info(TICKET_SERVICE_LOG_TICKET_DROP_BY_USER, ticket.getId());
    }

    @Transactional(isolation = Isolation.SERIALIZABLE, rollbackFor = ServiceException.class)
    @Override
    public void setActiveTicketsAsDropByNotEndedExcursions(Long id) {
        Ticket ticket = findById(id);
        if(!TicketState.ACTIVE.equals(ticket.getState())) {
            throw new ServiceException(SERVICE_NAME, TICKET_SERVICE_EXCEPTION_TICKET_IS_NOT_ACTIVE);
        }
        checkExcursionForStart(excursionService.findById(ticket.getExcursionId()));

        ticket.setState(TicketState.DROP_BY_NOT_ENDED_EXCURSION);
        saveUtil(ticket);
        log.info(TICKET_SERVICE_LOG_TICKET_DROP_BY_NOT_ENDED_EXCURSION, ticket.getId());
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    @Override
    public void deleteNotActiveTickets() {
        deleteNotActiveTicketsNoBackCoins();
        deleteNotActiveTicketsBackCoins();
        log.info(TICKET_SERVICE_LOG_DELETE_NOT_ACTIVE_TICKETS);
    }

    @Transactional(isolation = Isolation.SERIALIZABLE, rollbackFor = ServiceException.class)
    @Override
    public void setActiveTicketsAsDropByEndedExcursions(List<Excursion> endedExcursions) {
        List<Long> endedExcursionIds = null;

        if(endedExcursions != null) {
            if (endedExcursions.size() > 0) {
                endedExcursionIds = endedExcursions.stream().map(Excursion::getId).collect(Collectors.toList());
                List<Ticket> tickets = ticketRepository.findByExcursionIdInAndState(endedExcursionIds, TicketState.ACTIVE);
                if(tickets != null) {
                    if(tickets.size() > 0) {
                        for(int i = 0; i < tickets.size(); i++) {
                            Ticket ticket = tickets.get(i);
                            ticket.setState(TicketState.DROP_BY_ENDED_EXCURSION);
                            tickets.set(i, ticket);
                        }
                    }
                }
            }
        }

        log.info(TICKET_SERVICE_LOG_TICKET_DROP_BY_ENDED_EXCURSIONS, endedExcursionIds);
    }

    @Transactional(isolation = Isolation.SERIALIZABLE, rollbackFor = ServiceException.class)
    @Override
    public void setActiveTicketsAsDropByWrongExcursions(List<Excursion> wrongExcursions) {
        List<Long> wrongExcursionsIds = null;
        if(wrongExcursions != null) {
            if(wrongExcursions.size() > 0) {
                wrongExcursionsIds = wrongExcursions.stream().map(Excursion::getId).collect(Collectors.toList());
                List<Ticket> tickets = ticketRepository.findByExcursionIdInAndState(wrongExcursionsIds, TicketState.ACTIVE);
                if(tickets != null) {
                    if(tickets.size() > 0) {
                        for(int i = 0; i < tickets.size(); i++) {
                            Ticket ticket = tickets.get(i);
                            ticket.setState(TicketState.DROP_BY_WRONG_EXCURSION);
                            tickets.set(i, ticket);
                        }
                    }
                }
            }
        }

        log.info(TICKET_SERVICE_LOG_TICKET_DROP_BY_WRONG_EXCURSIONS, wrongExcursionsIds);
    }

    @Override
    public List<Ticket> findTicketsForUserById(Long userId) {
        List<Ticket> tickets = ticketRepository.findByUserId(userId);
        log.info(TICKET_SERVICE_LOG_FIND_TICKETS_FOR_USER, tickets, userId);
        return tickets;
    }

    private void deleteNotActiveTicketsBackCoins() {
        ArrayList<TicketState> ticketStatesNoBackCoins = new ArrayList<>();
        ticketStatesNoBackCoins.add(TicketState.ACTIVE);
        ticketStatesNoBackCoins.add(TicketState.DROP_BY_ENDED_EXCURSION);

        List<Ticket> tickets = ticketRepository.findByStateNotIn(ticketStatesNoBackCoins);
        List<Ticket> ticketsForDelete = new ArrayList<>();

        for(Ticket t: tickets) {
            try {
                userService.coinsUpByExcursion(t.getUserId(), t.getCoinsCost());
            }
            catch (ServiceException e)
            {
                continue;
            }
            ticketsForDelete.add(t);
        }

        ticketRepository.deleteAll(ticketsForDelete);
    }

    private void deleteNotActiveTicketsNoBackCoins() {
        ticketRepository.deleteByState(TicketState.DROP_BY_ENDED_EXCURSION);
    }

    private void checkExcursionForStart(Excursion excursion) {
        if(LocalDateTime.now().isAfter(excursion.getStart())) {
            throw new ServiceException(SERVICE_NAME, TICKET_SERVICE_EXCEPTION_EXCURSION_STARTED);
        }
    }

    private Ticket saveUtil(Ticket ticketForSave) {
        Ticket savedTicket;
        try {
            savedTicket = ticketRepository.save(ticketForSave);
            entityManager.flush();
        } catch (ConstraintViolationException e) {
            throw new ServiceException(SERVICE_NAME, e.getConstraintViolations().iterator().next().getMessage());
        }

        return savedTicket;
    }

    public void setExcursionService(ExcursionService excursionService) {
        if(this.excursionService == null) {
            this.excursionService = excursionService;
        }
    }
}
