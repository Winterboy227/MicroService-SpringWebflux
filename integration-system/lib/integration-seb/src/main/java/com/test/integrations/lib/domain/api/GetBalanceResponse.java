package com.test.integrations.lib.domain.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@ToString
@EqualsAndHashCode
public class GetBalanceResponse {

    private final Account account;
    private final List<BalanceItemData> balances;

    @JsonCreator
    public GetBalanceResponse(@JsonProperty("account") final Account account,
                              @JsonProperty("balances") final List<BalanceItemData> balances) {
        this.account = account;
        this.balances = balances;
    }
}
