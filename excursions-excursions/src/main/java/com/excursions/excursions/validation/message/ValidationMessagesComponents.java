package com.excursions.excursions.validation.message;

public class ValidationMessagesComponents {
    public static final String STRING_FIELD_NOTNULL_MIN_MAX = " must exist and have size ";
    public static final String STRING_FIELD_NOTNULL_MIN_MAX_DIVIDE = ":";

    public static final String LOCAL_DATA_TIME_FIELD_NOTNULL_AFTER_NOW = "  must exist and be in future";
    public static final String LOCAL_DATA_TIME_FIELD_NOTNULL_AFTER_ANOTHER_TIME = "  must exist and be after ";

    public static final String INTEGER_FIELD_NOTNULL_MIN_MAX = STRING_FIELD_NOTNULL_MIN_MAX;
    public static final String INTEGER_FIELD_NOTNULL_MIN_MAX_DIVIDE = STRING_FIELD_NOTNULL_MIN_MAX_DIVIDE;

    public static final String LIST_ID_FIELD_NOTNULL_NOT_EMPTY_EXIST = " must exist, be not empty and include exist id";

    public static final String LONG_FIELD_NOTNULL_NOT_NEGATIVE = " must exist, be not negative and be smaller than max long";

    public static final String LONG_FIELD_NOTNULL = " must exist";

    public static final String ENUM_FIELD_NOTNULL = " must exist";
}
