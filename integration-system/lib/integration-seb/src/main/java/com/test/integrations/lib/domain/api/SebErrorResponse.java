package com.test.integrations.lib.domain.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class SebErrorResponse {
    @JsonProperty("error_description")
    public final String errorMessage;

    @JsonProperty("error")
    public final String error;

    @JsonCreator
    public SebErrorResponse(@JsonProperty("error_description") final String errorMessage,
                            @JsonProperty("error") final String error) {
        this.errorMessage = errorMessage;
        this.error = error;
    }
}
