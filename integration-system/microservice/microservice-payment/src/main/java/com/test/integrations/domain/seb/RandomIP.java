package com.test.integrations.domain.seb;

import com.test.integrations.domain.seb.validation.Predicates;
import com.test.integrations.domain.seb.validation.Validator;
import io.vavr.control.Validation;

public class RandomIP {

    private static final String NULL_MESSAGE = "randomIp cannot be null";
    private static final String EMPTY_MESSAGE = "randomIp cannot be empty";

    private final String randomIp;

    private RandomIP(final String randomIp) {
        this.randomIp = randomIp;
    }

    public String getValue() {
        return randomIp;
    }

    public static Validation<String, RandomIP> validate(final String randomIp) {
        return Validator.validate(randomIp)
                .nonNull(NULL_MESSAGE)
                .and(Predicates.nonEmptyString(), EMPTY_MESSAGE)
                .map(RandomIP::new)
                .toValidation();
    }
}
