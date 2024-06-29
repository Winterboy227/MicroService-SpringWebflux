package com.test.integrations.api.beans.v1;

import lombok.Value;

@Value
public class Account {

    String iBan;
    String bBan;
    String status;
    String ownerName;
}
