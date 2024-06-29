package com.test.integrations.domain.seb;

import com.test.integrations.domain.seb.validation.Predicates;
import com.test.integrations.domain.seb.validation.Validator;
import io.vavr.control.Validation;

public class Bban {

    private static final String NULL_MESSAGE = "bban cannot be null";
    private static final String EMPTY_MESSAGE = "bban cannot be empty";

    private final String bban;

    private Bban(final String bban) {
        this.bban = bban;
    }

    public String getValue() {
        return bban;
    }

    public static Validation<String, Bban> validate(final String bban) {
        return Validator.validate(bban)
                .nonNull(NULL_MESSAGE)
                .and(Predicates.nonEmptyString(), EMPTY_MESSAGE)
                .map(Bban::new)
                .toValidation();
    }
}
