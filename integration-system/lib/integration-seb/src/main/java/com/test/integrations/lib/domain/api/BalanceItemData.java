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
public class BalanceItemData {
    private final String balanceType;
    private final BalanceAmount balanceAmount;
    private final String creditLimitIncluded;

    @JsonCreator
    public BalanceItemData(@JsonProperty("balanceType") final String balanceType,
                           @JsonProperty("balanceAmount") final BalanceAmount balanceAmount,
                           @JsonProperty("creditLimitIncluded") final String creditLimitIncluded) {
        this.balanceType = balanceType;
        this.balanceAmount = balanceAmount;
        this.creditLimitIncluded = creditLimitIncluded;
    }
}
