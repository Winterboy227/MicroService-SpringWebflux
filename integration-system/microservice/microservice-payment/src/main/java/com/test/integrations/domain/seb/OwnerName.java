package com.test.integrations.domain.seb;

import com.test.integrations.domain.seb.validation.Predicates;
import com.test.integrations.domain.seb.validation.Validator;
import io.vavr.control.Validation;

public class OwnerName {

    private static final String NULL_MESSAGE = "name cannot be null";
    private static final String EMPTY_MESSAGE = "name cannot be empty";

    private final String ownerName;

    private OwnerName(final String ownerName) {
        this.ownerName = ownerName;
    }

    public String getValue() {
        return ownerName;
    }

    public static Validation<String, OwnerName> validate(final String ownerName) {
        return Validator.validate(ownerName)
                .nonNull(NULL_MESSAGE)
                .and(Predicates.nonEmptyString(), EMPTY_MESSAGE)
                .map(OwnerName::new)
                .toValidation();
    }
}
