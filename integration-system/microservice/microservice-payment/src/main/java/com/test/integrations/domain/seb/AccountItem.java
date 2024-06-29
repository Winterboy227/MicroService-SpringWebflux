package com.test.integrations.domain.seb;

import lombok.Value;

@Value
public class AccountItem {

    Bban bban;
    Iban Iban;
    OwnerName ownerName;
    Status status;
}
