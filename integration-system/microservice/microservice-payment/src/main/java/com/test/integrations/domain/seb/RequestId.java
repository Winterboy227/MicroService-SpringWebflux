package com.test.integrations.domain.seb;

import com.test.integrations.domain.seb.validation.Predicates;
import com.test.integrations.domain.seb.validation.Validator;
import io.vavr.control.Validation;

public class RequestId {

    private static final String NULL_MESSAGE = "requestId cannot be null";
    private static final String EMPTY_MESSAGE = "requestId cannot be empty";

    private final String requestId;

    private RequestId(final String requestId) {
        this.requestId = requestId;
    }

    public String getValue() {
        return requestId;
    }

    public static Validation<String, RequestId> validate(final String requestId) {
        return Validator.validate(requestId)
                .nonNull(NULL_MESSAGE)
                .and(Predicates.nonEmptyString(), EMPTY_MESSAGE)
                .map(RequestId::new)
                .toValidation();
    }
}
