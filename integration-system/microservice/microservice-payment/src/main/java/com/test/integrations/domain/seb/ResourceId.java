package com.test.integrations.domain.seb;

import com.test.integrations.domain.seb.validation.Predicates;
import com.test.integrations.domain.seb.validation.Validator;
import io.vavr.control.Validation;

public class ResourceId {

    private static final String NULL_MESSAGE = "resourceId cannot be null";
    private static final String EMPTY_MESSAGE = "resourceId cannot be empty";

    private final String resourceId;

    private ResourceId(final String resourceId) {
        this.resourceId = resourceId;
    }

    public String getValue() {
        return resourceId;
    }

    public static Validation<String, ResourceId> validate(final String resourceId) {
        return Validator.validate(resourceId)
                .nonNull(NULL_MESSAGE)
                .and(Predicates.nonEmptyString(), EMPTY_MESSAGE)
                .map(ResourceId::new)
                .toValidation();
    }
}
