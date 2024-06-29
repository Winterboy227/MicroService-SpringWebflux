package com.test.integrations.lib.domain.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@ToString
@EqualsAndHashCode
public class BalanceAmount {

    private final String amount;
    private final String currency;

    @JsonCreator
    public BalanceAmount(@JsonProperty("amount") final String amount,
                         @JsonProperty("currency") final String currency) {
        this.amount = amount;
        this.currency = currency;
    }
}
