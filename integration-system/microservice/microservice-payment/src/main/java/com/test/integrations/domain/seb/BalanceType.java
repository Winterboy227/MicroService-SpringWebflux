package com.test.integrations.domain.seb;

import com.test.integrations.domain.seb.validation.Predicates;
import com.test.integrations.domain.seb.validation.Validator;
import io.vavr.control.Validation;


public class BalanceType {
    private static final String NULL_MESSAGE = "type cannot be null";
    private static final String EMPTY_MESSAGE = "type cannot be empty";

    private final String type;

    private BalanceType(final String type) {
        this.type = type;
    }

    public String getValue() {
        return type;
    }

    public static Validation<String, BalanceType> validate(final String type) {
        return Validator.validate(type)
                .nonNull(NULL_MESSAGE)
                .and(Predicates.nonEmptyString(), EMPTY_MESSAGE)
                .map(BalanceType::new)
                .toValidation();
    }
}
