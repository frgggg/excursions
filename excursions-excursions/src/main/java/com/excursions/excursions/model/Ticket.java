package com.excursions.excursions.model;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import static com.excursions.excursions.validation.message.ValidationMessagesComponents.*;

@Data
@Entity
@Table(name = "tickets")
public class Ticket {

    public static final String TICKET_EXCURSION_ID_FIELD_NAME = "excursion_id";
    public static final String TICKET_EXCURSION_ID_VALIDATION_MESSAGE = TICKET_EXCURSION_ID_FIELD_NAME + LONG_FIELD_NOTNULL;

    public static final String TICKET_COINS_COST_FIELD_NAME = "coinsCost";
    public static final long TICKET_COINS_COST_MIN = 1l;
    public static final String TICKET_COINS_COST_VALIDATION_MESSAGE = TICKET_COINS_COST_FIELD_NAME + LONG_FIELD_NOTNULL_NOT_NEGATIVE;

    public static final String TICKET_STATE_FIELD_NAME = "coinsCost";
    public static final String TICKET_STATE_VALIDATION_MESSAGE = TICKET_STATE_FIELD_NAME + ENUM_FIELD_NOTNULL;

    public static final String TICKET_USER_ID_FIELD_NAME = "userId";
    public static final String TICKET_USER_ID_VALIDATION_MESSAGE = TICKET_USER_ID_FIELD_NAME + LONG_FIELD_NOTNULL;

    @Id
    @SequenceGenerator(name = "tickets_sequence_gen", sequenceName = "tickets_sequence",
            initialValue = 1, allocationSize = 1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="tickets_sequence_gen")
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    @Column(name = "excursion_id", nullable = false)
    @NotNull(message = TICKET_EXCURSION_ID_VALIDATION_MESSAGE)
    private Long excursionId;

    @Column(name = "state", nullable = false)
    @NotNull(message = TICKET_STATE_VALIDATION_MESSAGE)
    @Enumerated(EnumType.STRING)
    private TicketState state;

    @Column(name = "coins_cost", nullable = false)
    @NotNull(message = TICKET_COINS_COST_VALIDATION_MESSAGE)
    @Min(value = TICKET_COINS_COST_MIN, message = TICKET_COINS_COST_VALIDATION_MESSAGE)
    private Long coinsCost;

    @Column(name = "user_id", nullable = false)
    @NotNull(message = TICKET_USER_ID_VALIDATION_MESSAGE)
    private Long userId;

    protected Ticket() {}

    public Ticket(Long excursionId, Long coinsCost, Long userId) {
        this.excursionId = excursionId;
        this.coinsCost = coinsCost;
        this.userId = userId;

        state = TicketState.ACTIVE;
    }
}
