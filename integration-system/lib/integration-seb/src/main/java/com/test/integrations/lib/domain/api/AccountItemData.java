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
public class AccountItemData {

    private final String resourceId;
    private final String iban;
    private final String bban;
    private final String ownerName;
    private final String product;
    private final String name;
    private final String status;

    @JsonCreator
    public AccountItemData(@JsonProperty("resourceId") final String resourceId,
                           @JsonProperty("iban") final String iban,
                           @JsonProperty("bban") final String  bban,
                           @JsonProperty("ownerName") final String  ownerName,
                           @JsonProperty("product") final String product,
                           @JsonProperty("name") final String name,
                           @JsonProperty("status") final String status) {
        this.resourceId = resourceId;
        this.iban = iban;
        this.ownerName = ownerName;
        this.product = product;
        this.name = name;
        this.bban = bban;
        this.status = status;
    }
}
