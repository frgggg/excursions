package com.excursions.excursions.validation;

import com.excursions.excursions.validation.impl.ExcursionStartStopValidationImpl;

import javax.validation.Constraint;
import java.lang.annotation.*;

@Documented
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy= ExcursionStartStopValidationImpl.class)
public @interface ExcursionStartStopValidation {
    String message() default "";

    Class<?>[] groups() default {};

    Class<? extends javax.validation.Payload>[] payload() default {};
}
