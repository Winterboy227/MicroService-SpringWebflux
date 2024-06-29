package com.test.integrations.lib.domain.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode
public class GetAccountResponse {
    @JsonProperty("accounts")
    private final List<AccountItemData> accountItems;

    public GetAccountResponse(@JsonProperty("accounts") final List<AccountItemData> accountItems) {
        this.accountItems = accountItems;
    }
}
