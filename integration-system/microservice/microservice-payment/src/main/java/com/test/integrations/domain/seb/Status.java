package com.test.integrations.domain.seb;

import com.test.integrations.domain.seb.validation.Predicates;
import com.test.integrations.domain.seb.validation.Validator;
import io.vavr.control.Validation;

public class Status {

    private static final String NULL_MESSAGE = "status cannot be null";
    private static final String EMPTY_MESSAGE = "status cannot be empty";

    private final String status;

    private Status(final String status) {
        this.status = status;
    }

    public String getValue() {
        return status;
    }

    public static Validation<String, Status> validate(final String status) {
        return Validator.validate(status)
                .nonNull(NULL_MESSAGE)
                .and(Predicates.nonEmptyString(), EMPTY_MESSAGE)
                .map(Status::new)
                .toValidation();
    }
}
