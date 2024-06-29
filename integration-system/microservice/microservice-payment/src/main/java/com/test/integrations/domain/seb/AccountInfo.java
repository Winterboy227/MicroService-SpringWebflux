package com.test.integrations.domain.seb;

import io.vavr.collection.List;
import lombok.Value;

@Value
public class AccountInfo {

   List<AccountItem> accountItems;


}
