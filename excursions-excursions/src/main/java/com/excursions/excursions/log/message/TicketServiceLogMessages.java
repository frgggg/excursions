package com.excursions.excursions.log.message;

public class TicketServiceLogMessages {
    public static final String TICKET_SERVICE_LOG_NEW_TICKET = "New ticket: {}";

    public static final String TICKET_SERVICE_LOG_FIND_EXCURSION = "find ticket with id {}";

    public static final String TICKET_SERVICE_LOG_FIND_ALL = "find all tickets";

    public static final String TICKET_SERVICE_LOG_FIND_TICKETS_FOR_USER = "find tickets {} for user with id {}";

    public static final String TICKET_SERVICE_LOG_TICKET_DROP_BY_USER = "ticket with id {} drop by user";

    public static final String TICKET_SERVICE_LOG_TICKET_DROP_BY_NOT_ENDED_EXCURSION = "ticket with id {} drop by not ended excursion";

    public static final String TICKET_SERVICE_LOG_TICKET_DROP_BY_WRONG_EXCURSIONS = "tickets for wrong excursions {} drop";
    public static final String TICKET_SERVICE_LOG_TICKET_DROP_BY_ENDED_EXCURSIONS = "tickets for ended excursions {} drop";

    public static final String TICKET_SERVICE_LOG_DELETE_NOT_ACTIVE_TICKETS = "delete not active tickets";

    public static final String TICKET_SERVICE_LOG_BACK_COINS = "back coins {} for user {}";
    public static final String TICKET_SERVICE_LOG_ERROR_BACK_COINS = "error in back coins {} for user {}";
}
