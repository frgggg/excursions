package com.excursions.excursions.repository;

import com.excursions.excursions.model.Ticket;
import com.excursions.excursions.model.TicketState;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface TicketRepository extends CrudRepository<Ticket, Long> {

    Long countByExcursionIdAndState(Long excursionId, TicketState ticketState);

    List<Ticket> findByUserId(Long userId);

    List<Ticket> findByExcursionIdInAndState(List<Long> excursionId, TicketState ticketState);

    List<Ticket> findByStateNotIn(List<TicketState> ticketStates);

    @Modifying
    @Query(value = "update Ticket t set t.state=?2 where t.id=?1 and t.state=?3")
    int updateTicketStatus(Long id, TicketState newTicketState, TicketState oldTicketState);

    @Modifying
    @Query(value = "update Ticket t set t.state=?2 where t.id in ?1 and t.state=?3")
    int updateTicketsStatus(List<Long> id, TicketState newTicketState, TicketState oldTicketState);

    @Modifying
    void deleteByState(TicketState ticketState);
}
