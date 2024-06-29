package com.test.integrations.domain.seb;

import lombok.Value;

@Value
public class BalanceItem {
    BalanceType balanceType;
    BalanceAmount balanceAmount;
}
