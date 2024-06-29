package com.test.integrations.domain.seb;

import com.test.integrations.domain.seb.validation.Predicates;
import com.test.integrations.domain.seb.validation.Validator;
import io.vavr.control.Validation;


public class BalanceAmount {
    private static final String NULL_MESSAGE = "amount cannot be null";
    private static final String EMPTY_MESSAGE = "amount cannot be empty";

    private final String amount;

    private BalanceAmount(final String amount) {
        this.amount = amount;
    }

    public String getValue() {
        return amount;
    }

    public static Validation<String, BalanceAmount> validate(final String amount) {
        return Validator.validate(amount)
                .nonNull(NULL_MESSAGE)
                .and(Predicates.nonEmptyString(), EMPTY_MESSAGE)
                .map(BalanceAmount::new)
                .toValidation();
    }
}
