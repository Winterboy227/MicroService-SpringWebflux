package com.test.integrations.api.beans.v1;

import lombok.Value;

@Value
public class Balance {
    String balanceType;
    String amount;
}
