package com.excursions.excursions.validation.impl;

import com.excursions.excursions.exception.ServiceException;
import com.excursions.excursions.model.Excursion;
import com.excursions.excursions.service.PlaceService;
import com.excursions.excursions.validation.ExcursionStartStopValidation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;
import java.util.List;

import static com.excursions.excursions.model.Excursion.*;

public class ExcursionStartStopValidationImpl implements ConstraintValidator<ExcursionStartStopValidation, Excursion> {

    private static PlaceService placeService = null;

    public static void initPlaceService(PlaceService placeService) {
        if(ExcursionStartStopValidationImpl.placeService == null) {
            ExcursionStartStopValidationImpl.placeService = placeService;
        }
    }

    @Override
    public boolean isValid(Excursion excursion, ConstraintValidatorContext context) {
        LocalDateTime start = excursion.getStart();
        if(start == null) {
            setContextForWrongStart(context);
            return false;
        } else if(start.isBefore(LocalDateTime.now())) {
            setContextForWrongStart(context);
            return false;
        }

        LocalDateTime stop = excursion.getStop();
        if(stop == null) {
            setContextForWrongStop(context);
            return false;
        } else if(stop.isBefore(start)) {
            setContextForWrongStop(context);
            return false;
        }

        List<Long> placesIds = excursion.getPlacesIds();
        if(placesIds == null) {
            setContextForWrongPlacesIds(context);
            return false;
        } else if(placesIds.size() < 1) {
            setContextForWrongPlacesIds(context);
            return false;
        } else {
            try {
                if(placeService.getNotExistPlacesIds(placesIds).size() > 0) {
                    setContextForWrongPlacesIds(context);
                    return false;
                }
            } catch (ServiceException e) {
                context.disableDefaultConstraintViolation();
                context
                        .buildConstraintViolationWithTemplate(e.getMessage())
                        .addConstraintViolation();
                return false;
            }
        }

        return true;
    }

    private void setContextForWrongPlacesIds(ConstraintValidatorContext context) {
        context.disableDefaultConstraintViolation();
        context
                .buildConstraintViolationWithTemplate(EXCURSION_PLACES_IDS_VALIDATION_MESSAGE)
                .addConstraintViolation();
    }

    private void setContextForWrongStart(ConstraintValidatorContext context) {
        context.disableDefaultConstraintViolation();
        context
                .buildConstraintViolationWithTemplate(EXCURSION_START_VALIDATION_MESSAGE)
                .addConstraintViolation();
    }

    private void setContextForWrongStop(ConstraintValidatorContext context) {
        context.disableDefaultConstraintViolation();
        context
                .buildConstraintViolationWithTemplate(EXCURSION_STOP_VALIDATION_MESSAGE)
                .addConstraintViolation();
    }
}
