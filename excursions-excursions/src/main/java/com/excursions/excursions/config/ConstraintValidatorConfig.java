package com.excursions.excursions.config;

import com.excursions.excursions.service.PlaceService;
import com.excursions.excursions.validation.impl.ExcursionStartStopValidationImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConstraintValidatorConfig {

    @Autowired
    protected ConstraintValidatorConfig(PlaceService placeService) {
        ExcursionStartStopValidationImpl.initPlaceService(placeService);
    }
}
