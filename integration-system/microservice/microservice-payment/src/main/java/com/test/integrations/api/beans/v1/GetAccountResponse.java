package com.test.integrations.api.beans.v1;

import java.util.List;
import lombok.Value;

@Value
public class GetAccountResponse {
    List<Account> accountList;

}
