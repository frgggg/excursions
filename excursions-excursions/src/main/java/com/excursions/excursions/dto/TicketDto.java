package com.excursions.excursions.dto;

import com.excursions.excursions.model.TicketState;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import static com.excursions.excursions.model.Ticket.*;

@Data
@Builder
@AllArgsConstructor
public class TicketDto {

    private Long id;

    @NotNull(message = TICKET_EXCURSION_ID_VALIDATION_MESSAGE)
    private Long excursionId;

    @NotNull(message = TICKET_STATE_VALIDATION_MESSAGE)
    private TicketState state;

    @NotNull(message = TICKET_COINS_COST_VALIDATION_MESSAGE)
    @Min(value = TICKET_COINS_COST_MIN, message = TICKET_COINS_COST_VALIDATION_MESSAGE)
    private Long coinsCost;

    @NotNull(message = TICKET_USER_ID_VALIDATION_MESSAGE)
    private Long userId;

    protected TicketDto(){}
}
