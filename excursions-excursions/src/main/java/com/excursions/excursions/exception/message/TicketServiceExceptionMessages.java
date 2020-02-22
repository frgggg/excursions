package com.excursions.excursions.exception.message;

public class TicketServiceExceptionMessages {
    public static final String TICKET_SERVICE_EXCEPTION_NOT_EXIST_EXCURSION = "try find not exist ticket with id = %s";
    public static final String TICKET_SERVICE_EXCEPTION_WRONG_COST = "try to bay ticket with wrong cost";
    public static final String TICKET_SERVICE_EXCEPTION_NEW_TICKET_NOT_ENABLE = "new tickets for current excursion not enabled";
    public static final String TICKET_SERVICE_EXCEPTION_MAX_PEOPLE_COUNT = "current excursion have max people count";
    public static final String TICKET_SERVICE_EXCEPTION_EXCURSION_STARTED = "current excursion started or time for back is end";

    public static final String TICKET_SERVICE_EXCEPTION_TICKET_IS_NOT_ACTIVE = "current ticket is not active";

    public static final String TICKET_SERVICE_EXCEPTION_CANT_DROP_TICKETS_FOR_ENDED_EXCURSION = "can't delete tickets for ended excursions";
}
