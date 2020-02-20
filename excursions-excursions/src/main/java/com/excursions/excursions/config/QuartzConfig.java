package com.excursions.excursions.config;

import com.excursions.excursions.quartz.DeleteEndedExcursions;
import com.excursions.excursions.quartz.DeleteNotEndedExcursionsByNotExistPlaces;
import com.excursions.excursions.quartz.DropNoActiveTickets;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QuartzConfig {

    @Value("${excursion.ended.quartz}")
    private String deleteEndedExcursionsCircleConfig;

    @Value("${excursion.wrong-by-places.quartz}")
    private String deleteWrongExcursionsByPlacesCircleConfig;

    @Value("${tickets.delete.quartz}")
    private String deleteNotActiveTicketsCircleConfig;

    private static final String JOB_IDENTITY_POSTFIX = "-JobDetail-Identity";
    private static final String TRIGGER_IDENTITY_POSTFIX = "-JobDetail-Identity";

    @Bean("deleteEndedExcursionsJobDetail")
    public JobDetail deleteEndedExcursionsJobDetail(){
        return JobBuilder.newJob(DeleteEndedExcursions.class)
                .withIdentity(DeleteEndedExcursions.class.getSimpleName() + JOB_IDENTITY_POSTFIX)
                .storeDurably().build();
    }

    @Bean
    public Trigger deleteEndedExcursionsJobTrigger(@Qualifier("deleteEndedExcursionsJobDetail") JobDetail findDebtorsJobDetail)
    {
        return TriggerBuilder.newTrigger().forJob(findDebtorsJobDetail)
                .withIdentity(DeleteEndedExcursions.class.getSimpleName() + TRIGGER_IDENTITY_POSTFIX)
                .withSchedule(CronScheduleBuilder.cronSchedule(deleteEndedExcursionsCircleConfig))
                .build();
    }



    @Bean("deleteNotEndedExcursionsByNotExistPlacesJobDetail")
    public JobDetail deleteNotEndedExcursionsByNotExistPlacesJobDetail(){
        return JobBuilder.newJob(DeleteNotEndedExcursionsByNotExistPlaces.class)
                .withIdentity(DeleteNotEndedExcursionsByNotExistPlaces.class.getSimpleName() + JOB_IDENTITY_POSTFIX)
                .storeDurably().build();
    }

    @Bean
    public Trigger deleteNotEndedExcursionsByNotExistPlacesJobTrigger(@Qualifier("deleteNotEndedExcursionsByNotExistPlacesJobDetail") JobDetail findDebtorsJobDetail)
    {
        return TriggerBuilder.newTrigger().forJob(findDebtorsJobDetail)
                .withIdentity(DeleteNotEndedExcursionsByNotExistPlaces.class.getSimpleName() + TRIGGER_IDENTITY_POSTFIX)
                .withSchedule(CronScheduleBuilder.cronSchedule(deleteWrongExcursionsByPlacesCircleConfig))
                .build();
    }



    @Bean("deleteNotActiveTicketsJobDetail")
    public JobDetail deleteNotActiveTicketsJobDetail(){
        return JobBuilder.newJob(DropNoActiveTickets.class)
                .withIdentity(DropNoActiveTickets.class.getSimpleName() + JOB_IDENTITY_POSTFIX)
                .storeDurably().build();
    }

    @Bean
    public Trigger deleteNotActiveTicketsJobTrigger(@Qualifier("deleteNotActiveTicketsJobDetail") JobDetail findDebtorsJobDetail)
    {
        return TriggerBuilder.newTrigger().forJob(findDebtorsJobDetail)
                .withIdentity(DropNoActiveTickets.class.getSimpleName() + TRIGGER_IDENTITY_POSTFIX)
                .withSchedule(CronScheduleBuilder.cronSchedule(deleteNotActiveTicketsCircleConfig))
                .build();
    }
}
