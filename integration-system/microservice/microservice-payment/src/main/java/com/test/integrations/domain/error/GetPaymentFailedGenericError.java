package com.test.integrations.domain.error;

public class GetPaymentFailedGenericError extends RuntimeException {
    public GetPaymentFailedGenericError(final String message) {
        super(message);
    }
}
