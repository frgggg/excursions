package com.excursions.excursions.quartz;

import com.excursions.excursions.service.TicketService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

import static com.excursions.excursions.log.message.QuartzLogMessages.QUARTZ_LOG_JOB_IN_PROCESS;

@Slf4j
public class DropNoActiveTickets implements Job {

    private TicketService ticketService;

    @Autowired
    private DropNoActiveTickets(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        ticketService.deleteNotActiveTickets();
        log.info(QUARTZ_LOG_JOB_IN_PROCESS, this.getClass().getSimpleName());
    }
}
