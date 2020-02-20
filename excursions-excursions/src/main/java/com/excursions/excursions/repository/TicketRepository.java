package com.excursions.excursions.repository;

import com.excursions.excursions.model.Ticket;
import com.excursions.excursions.model.TicketState;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface TicketRepository extends CrudRepository<Ticket, Long> {

    Long countByExcursionIdAndState(Long excursionId, TicketState ticketState);

    List<Ticket> findByUserId(Long userId);

    List<Ticket> findByExcursionIdInAndState(List<Long> excursionId, TicketState ticketState);

    List<Ticket> findByStateNotIn(List<TicketState> ticketStates);


    void deleteByState(TicketState ticketState);
}
