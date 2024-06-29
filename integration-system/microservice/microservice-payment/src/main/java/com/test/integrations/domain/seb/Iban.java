package com.test.integrations.domain.seb;

import com.test.integrations.domain.seb.validation.Predicates;
import com.test.integrations.domain.seb.validation.Validator;
import io.vavr.control.Validation;

public class Iban {

    private static final String NULL_MESSAGE = "iban cannot be null";
    private static final String EMPTY_MESSAGE = "iban cannot be empty";

    private final String iban;

    private Iban(final String iban) {
        this.iban = iban;
    }

    public String getValue() {
        return iban;
    }

    public static Validation<String, Iban> validate(final String iban) {
        return Validator.validate(iban)
                .nonNull(NULL_MESSAGE)
                .and(Predicates.nonEmptyString(), EMPTY_MESSAGE)
                .map(Iban::new)
                .toValidation();
    }
}
