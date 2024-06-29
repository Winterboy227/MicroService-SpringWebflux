package com.test.integrations.api.beans.v1;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class ErrorResponse {
    @JsonProperty("errorMessage")
    public final String errorMessage;

    @JsonProperty("errorCode")
    public final Integer errorCode;

    public ErrorResponse(final String errorMessage, final Integer errorCode) {
        this.errorMessage = errorMessage;
        this.errorCode = errorCode;
    }
}
