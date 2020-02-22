package com.excursions.excursions.model;

import com.excursions.excursions.validation.ExcursionStartStopValidation;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.List;

import static com.excursions.excursions.validation.message.ValidationMessagesComponents.*;

@Data
@ExcursionStartStopValidation
@Entity
@Table(
        name = "excursions",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"name", "start"})}
)
public class Excursion {

    public static final String EXCURSION_NAME_FIELD_NAME = "name";
    public static final int EXCURSION_NAME_LEN_MIN = 1;
    public static final int EXCURSION_NAME_LEN_MAX = 90;
    public static final String EXCURSION_NAME_VALIDATION_MESSAGE =
            EXCURSION_NAME_FIELD_NAME + STRING_FIELD_NOTNULL_MIN_MAX + EXCURSION_NAME_LEN_MIN + STRING_FIELD_NOTNULL_MIN_MAX_DIVIDE + EXCURSION_NAME_LEN_MAX;

    public static final String EXCURSION_START_FIELD_NAME = "start";
    public static final String EXCURSION_START_VALIDATION_MESSAGE = EXCURSION_START_FIELD_NAME + LOCAL_DATA_TIME_FIELD_NOTNULL_AFTER_NOW;

    public static final String EXCURSION_STOP_FIELD_NAME = "stop";
    public static final String EXCURSION_STOP_VALIDATION_MESSAGE = EXCURSION_STOP_FIELD_NAME + LOCAL_DATA_TIME_FIELD_NOTNULL_AFTER_ANOTHER_TIME + EXCURSION_START_FIELD_NAME;

    public static final String EXCURSION_PEOPLE_COUNT_FIELD_NAME = "peopleCount";
    public static final int EXCURSION_PEOPLE_COUNT_VALUE_MIN = 1;
    public static final int EXCURSION_PEOPLE_COUNT_VALUE_MAX = 100;
    public static final String EXCURSION_PEOPLE_COUNT_VALIDATION_MESSAGE =
            EXCURSION_PEOPLE_COUNT_FIELD_NAME + INTEGER_FIELD_NOTNULL_MIN_MAX + EXCURSION_PEOPLE_COUNT_VALUE_MIN + INTEGER_FIELD_NOTNULL_MIN_MAX_DIVIDE + EXCURSION_PEOPLE_COUNT_VALUE_MAX;

    public static final String EXCURSION_COINS_COST_FIELD_NAME = "coinsCost";
    public static final long EXCURSION_COINS_COST_MIN = 1l;
    public static final String EXCURSION_COINS_COST_VALIDATION_MESSAGE = EXCURSION_COINS_COST_FIELD_NAME + LONG_FIELD_NOTNULL_NOT_NEGATIVE;

    public static final String EXCURSION_PLACES_IDS_FIELD_NAME = "placesIds";
    public static final String EXCURSION_PLACES_IDS_VALIDATION_MESSAGE = EXCURSION_PLACES_IDS_FIELD_NAME + " must be mot null, not empty and include exist places ids";

    @Id
    @SequenceGenerator(name = "excursions_sequence_gen", sequenceName = "excursions_sequence",
            initialValue = 1, allocationSize = 1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="excursions_sequence_gen")
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    @Column(name = "name", length = EXCURSION_NAME_LEN_MAX, nullable = false)
    @NotNull(message = EXCURSION_NAME_VALIDATION_MESSAGE)
    @Size(min = EXCURSION_NAME_LEN_MIN, max = EXCURSION_NAME_LEN_MAX, message = EXCURSION_NAME_VALIDATION_MESSAGE)
    private String name;

    @Column(name = "start", nullable = false)
    private LocalDateTime start;

    @Column(name = "stop", nullable = false)
    private LocalDateTime stop;

    @Column(name = "coins_cost", nullable = false)
    @NotNull(message = EXCURSION_COINS_COST_VALIDATION_MESSAGE)
    @Min(value = EXCURSION_COINS_COST_MIN, message = EXCURSION_COINS_COST_VALIDATION_MESSAGE)
    @Max(value = Long.MAX_VALUE, message = EXCURSION_COINS_COST_VALIDATION_MESSAGE)
    private Long coinsCost;

    @Column(name = "people_count", nullable = false)
    @Min(value = EXCURSION_PEOPLE_COUNT_VALUE_MIN, message = EXCURSION_PEOPLE_COUNT_VALIDATION_MESSAGE)
    @Max(value = EXCURSION_PEOPLE_COUNT_VALUE_MAX, message = EXCURSION_PEOPLE_COUNT_VALIDATION_MESSAGE)
    private Integer peopleCount;

    @ElementCollection(targetClass = Long.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "excursion_places", joinColumns = @JoinColumn(name = "excursion_id"))
    @Column(name = "place_id")
    private List<Long> placesIds;

    @Column(name = "enable_new_tickets")
    private Boolean enableNewTickets;

    protected Excursion() {}

    public Excursion(String name,  LocalDateTime start,  LocalDateTime stop, Integer peopleCount, Long coinsCost, List<Long> placesIds) {
        this.name = name;
        this.start = start;
        this.stop = stop;
        this.peopleCount = peopleCount;
        this.coinsCost = coinsCost;
        this.placesIds = placesIds;
        enableNewTickets = true;
    }

    public void setEnableNewTickets(Boolean enableNewTickets) {
        this.enableNewTickets = enableNewTickets;
    }

    public Boolean getEnableNewTickets() {
        return enableNewTickets;
    }
}
