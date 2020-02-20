package com.excursions.excursions.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.*;

import java.time.LocalDateTime;
import java.util.List;

import static com.excursions.excursions.model.Excursion.*;

@Data
@Builder
@AllArgsConstructor
public class ExcursionDto {
    private Long id;

    @NotNull(message = EXCURSION_NAME_VALIDATION_MESSAGE)
    @Size(min = EXCURSION_NAME_LEN_MIN, max = EXCURSION_NAME_LEN_MAX, message = EXCURSION_NAME_VALIDATION_MESSAGE)
    private String name;

    @NotNull(message = EXCURSION_START_VALIDATION_MESSAGE)
    private LocalDateTime start;

    @NotNull(message = EXCURSION_STOP_VALIDATION_MESSAGE)
    private LocalDateTime stop;

    @NotNull(message = EXCURSION_PEOPLE_COUNT_VALIDATION_MESSAGE)
    @Min(value = EXCURSION_PEOPLE_COUNT_VALUE_MIN, message = EXCURSION_PEOPLE_COUNT_VALIDATION_MESSAGE)
    @Max(value = EXCURSION_PEOPLE_COUNT_VALUE_MAX, message = EXCURSION_PEOPLE_COUNT_VALIDATION_MESSAGE)
    private Integer peopleCount;

    @NotNull(message = EXCURSION_COINS_COST_VALIDATION_MESSAGE)
    @Min(value = EXCURSION_COINS_COST_MIN, message = EXCURSION_COINS_COST_VALIDATION_MESSAGE)
    @Max(value = Long.MAX_VALUE, message = EXCURSION_COINS_COST_VALIDATION_MESSAGE)
    private Long coinsCost;

    @NotNull(message = EXCURSION_PLACES_IDS_VALIDATION_MESSAGE)
    @NotEmpty(message = EXCURSION_PLACES_IDS_VALIDATION_MESSAGE)
    private List<Long> placesIds;

    public ExcursionDto() {}
}
