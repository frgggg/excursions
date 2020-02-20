package com.excursions.excursions.quartz;

import com.excursions.excursions.exception.ServiceException;
import com.excursions.excursions.service.ExcursionService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

import static com.excursions.excursions.log.message.QuartzLogMessages.QUARTZ_LOG_JOB_EXCEPTION;
import static com.excursions.excursions.log.message.QuartzLogMessages.QUARTZ_LOG_JOB_IN_PROCESS;

@Slf4j
public class DeleteNotEndedExcursionsByNotExistPlaces implements Job {

    private ExcursionService excursionService;

    @Autowired
    private DeleteNotEndedExcursionsByNotExistPlaces(ExcursionService excursionService) {
        this.excursionService = excursionService;
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        try {
            excursionService.deleteNotEndedExcursionsByNotExistPlaces();
        } catch (ServiceException e) {
            log.info(QUARTZ_LOG_JOB_EXCEPTION, e.getMessage(), this.getClass().getSimpleName());
        }
        log.info(QUARTZ_LOG_JOB_IN_PROCESS, this.getClass().getSimpleName());
    }
}
