package com.excursions.excursions.config;

import com.excursions.excursions.service.impl.ExcursionServiceImpl;
import com.excursions.excursions.service.impl.TicketServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServiceConfig {

    @Autowired
    protected ServiceConfig(TicketServiceImpl ticketServiceImpl, ExcursionServiceImpl excursionServiceImpl) {
        ticketServiceImpl.setExcursionService(excursionServiceImpl);
        ticketServiceImpl.setSelf(ticketServiceImpl);
        excursionServiceImpl.setTicketService(ticketServiceImpl);
    }
}
