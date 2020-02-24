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
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import javax.validation.ConstraintViolationException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static com.excursions.excursions.exception.message.TicketServiceExceptionMessages.*;
import static com.excursions.excursions.log.message.TicketServiceLogMessages.*;
import static com.excursions.excursions.service.impl.util.ServicesUtil.isListNotNullNotEmpty;

@Slf4j
@Service
public class TicketServiceImpl implements TicketService {

    @Value("${ticket.drop-by-user-before-stop.day}")
    private String deleteByUserBeforeStartMinusDay;

    private TicketRepository ticketRepository;
    private ExcursionService excursionService;
    private UserService userService;
    private PlatformTransactionManager transactionManager;

    private static ArrayList<TicketState> ticketStatesNoBackCoins;
    static {
        ticketStatesNoBackCoins.add(TicketState.ACTIVE);
        ticketStatesNoBackCoins.add(TicketState.DROP_BY_ENDED_EXCURSION);
    }

    @Autowired
    protected TicketServiceImpl(
            TicketRepository ticketRepository,
            UserService userService,
            PlatformTransactionManager transactionManager
    ) {
        this.ticketRepository = ticketRepository;
        this.userService = userService;
        this.transactionManager = transactionManager;
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    @Override
    public Ticket create(Long userId, Long excursionId, Long expectedCoinsCost) {
        Ticket savedTicket;

        try {
            savedTicket = saveUtil(userId, excursionId, expectedCoinsCost);
        } catch (ConstraintViolationException e) {
            throw new ServiceException(e.getConstraintViolations().iterator().next().getMessage());
        } catch (Exception e) {
            throw new ServiceException(e.getMessage());
        }

        log.info(TICKET_SERVICE_LOG_NEW_TICKET, savedTicket);
        return savedTicket;
    }

    @Override
    public List<Ticket> findAll() {
        log.info(TICKET_SERVICE_LOG_FIND_ALL);
        return StreamSupport.stream(ticketRepository.findAll().spliterator(), false)
                .collect(Collectors.toList());
    }

    @Override
    public Ticket findById(Long id) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new ServiceException(
                        String.format(TICKET_SERVICE_EXCEPTION_NOT_EXIST_EXCURSION, id)
                        )
                );
        log.info(TICKET_SERVICE_LOG_FIND_EXCURSION, ticket);
        return ticket;
    }

    @Override
    public void setActiveTicketsAsDropByUser(Long id) {
        Ticket ticket = findById(id);
        if(!TicketState.ACTIVE.equals(ticket.getState())) {
            throw new ServiceException( TICKET_SERVICE_EXCEPTION_TICKET_IS_NOT_ACTIVE);
        }

        Excursion excursion = excursionService.findById(ticket.getExcursionId());

        if(LocalDateTime.now().plusDays(Integer.parseInt(deleteByUserBeforeStartMinusDay)).isAfter(excursion.getStart())) {
            throw new ServiceException(TICKET_SERVICE_EXCEPTION_EXCURSION_STARTED);
        }

        if(ticketRepository.updateTicketStatus(id, TicketState.DROP_BY_USER, TicketState.ACTIVE) == 0) {
            throw new ServiceException(TICKET_SERVICE_EXCEPTION_TICKET_IS_NOT_ACTIVE);
        }
        log.info(TICKET_SERVICE_LOG_TICKET_DROP_BY_USER, ticket.getId());
    }

    @Override
    public void setActiveTicketsAsDropByNotEndedExcursions(Long id) {
        Ticket ticket = findById(id);
        if(!TicketState.ACTIVE.equals(ticket.getState())) {
            throw new ServiceException(TICKET_SERVICE_EXCEPTION_TICKET_IS_NOT_ACTIVE);
        }
        checkExcursionForStart(excursionService.findById(ticket.getExcursionId()));

        if(ticketRepository.updateTicketStatus(id, TicketState.DROP_BY_NOT_ENDED_EXCURSION, TicketState.ACTIVE) == 0) {
            throw new ServiceException(TICKET_SERVICE_EXCEPTION_TICKET_IS_NOT_ACTIVE);
        }

        log.info(TICKET_SERVICE_LOG_TICKET_DROP_BY_NOT_ENDED_EXCURSION, ticket.getId());
    }

    @Override
    public void deleteNotActiveTickets() {
        try {
            deleteNotActiveTicketsNoBackCoins();
        } catch (Exception e) {
            log.error("deleteNotActiveTicketsNoBackCoins: " + e.getMessage());
        }

        try {
            deleteNotActiveTicketsBackCoins();
        } catch (Exception e) {
            log.error("deleteNotActiveTicketsBackCoins: " + e.getMessage());
        }
        log.info(TICKET_SERVICE_LOG_DELETE_NOT_ACTIVE_TICKETS);
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    @Override
    public void setActiveTicketsAsDropByEndedExcursions(List<Excursion> endedExcursions) {
        List<Long> endedExcursionIds = null;

        if(isListNotNullNotEmpty(endedExcursions)) {
            endedExcursionIds = endedExcursions.stream().map(Excursion::getId).collect(Collectors.toList());
            List<Ticket> tickets = ticketRepository.findByExcursionIdInAndState(endedExcursionIds, TicketState.ACTIVE);
            if(isListNotNullNotEmpty(tickets)) {
                List<Long> ticketsIds = tickets.stream().map(Ticket::getId).collect(Collectors.toList());
                int updateResult = ticketRepository.updateTicketsStatus(
                        ticketsIds,
                        TicketState.DROP_BY_ENDED_EXCURSION,
                        TicketState.ACTIVE
                );
                if(updateResult == 0) {
                    throw new ServiceException(TICKET_SERVICE_EXCEPTION_CANT_DROP_TICKETS_FOR_ENDED_EXCURSION);
                }
            }
        }

        log.info(TICKET_SERVICE_LOG_TICKET_DROP_BY_ENDED_EXCURSIONS, endedExcursionIds);
    }

    @Override
    public void setActiveTicketsAsDropByWrongExcursions(List<Excursion> wrongExcursions) {
        List<Long> wrongExcursionsIds = null;
        if(isListNotNullNotEmpty(wrongExcursions)) {
            wrongExcursionsIds = wrongExcursions.stream().map(Excursion::getId).collect(Collectors.toList());
            List<Ticket> tickets = ticketRepository.findByExcursionIdInAndState(wrongExcursionsIds, TicketState.ACTIVE);
            if(isListNotNullNotEmpty(tickets)) {
                List<Long> ticketsIds = tickets.stream().map(Ticket::getId).collect(Collectors.toList());
                ticketRepository.updateTicketsStatus(
                        ticketsIds,
                        TicketState.DROP_BY_WRONG_EXCURSION,
                        TicketState.ACTIVE
                );
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
        List<Ticket> tickets = ticketRepository.findByStateNotIn(ticketStatesNoBackCoins);

        for(Ticket t: tickets) {
            try {
                deleteNotActiveTicketBackCoins(t);
                log.info(TICKET_SERVICE_LOG_BACK_COINS, t.getCoinsCost(), t.getUserId());
            } catch (Exception e) {
                log.error(TICKET_SERVICE_LOG_ERROR_BACK_COINS, t.getCoinsCost(), t.getUserId());
            }
        }
    }
    
    public void deleteNotActiveTicketBackCoins(Ticket t) {
        TransactionTemplate template = new TransactionTemplate(transactionManager);
        template.execute(
                new TransactionCallbackWithoutResult() {
                    @Override
                    protected void doInTransactionWithoutResult(TransactionStatus status) {
                            ticketRepository.delete(t);
                            userService.coinsUpByExcursion(t.getUserId(), t.getCoinsCost());
                    }
                }
        );
    }

    private void deleteNotActiveTicketsNoBackCoins() {
        ticketRepository.deleteByState(TicketState.DROP_BY_ENDED_EXCURSION);
    }

    private void checkExcursionForStart(Excursion excursion) {
        if(LocalDateTime.now().isAfter(excursion.getStart())) {
            throw new ServiceException(TICKET_SERVICE_EXCEPTION_EXCURSION_STARTED);
        }
    }

    private Ticket saveUtil(Long userId, Long excursionId, Long expectedCoinsCost) {
        Excursion excursion = excursionService.findById(excursionId);

        checkExcursionForStart(excursion);

        if(!expectedCoinsCost.equals(excursion.getCoinsCost())) {
            throw new ServiceException(TICKET_SERVICE_EXCEPTION_WRONG_COST);
        }

        if(!excursion.getEnableNewTickets()) {
            throw new ServiceException(TICKET_SERVICE_EXCEPTION_NEW_TICKET_NOT_ENABLE);
        }

        if(ticketRepository.countByExcursionIdAndState(excursionId, TicketState.ACTIVE) >= excursion.getPeopleCount()) {
            throw new ServiceException(TICKET_SERVICE_EXCEPTION_MAX_PEOPLE_COUNT);
        }

        Ticket ticketForSave = new Ticket(excursionId, expectedCoinsCost, userId);
        Ticket savedTicket = ticketRepository.save(ticketForSave);
        userService.coinsDownByExcursion(userId, expectedCoinsCost);
        return savedTicket;
    }

    public void setExcursionService(ExcursionService excursionService) {
        if(this.excursionService == null) {
            this.excursionService = excursionService;
        }
    }
}
