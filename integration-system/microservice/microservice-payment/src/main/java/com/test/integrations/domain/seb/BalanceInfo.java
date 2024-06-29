package com.test.integrations.domain.seb;

import io.vavr.collection.List;
import lombok.Value;

@Value
public class BalanceInfo {

    Iban iban;
    List<BalanceItem> balanceItems;

}
