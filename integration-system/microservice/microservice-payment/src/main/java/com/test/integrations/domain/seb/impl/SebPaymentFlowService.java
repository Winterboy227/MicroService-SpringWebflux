package com.test.integrations.domain.seb.impl;

import com.test.integrations.domain.seb.AccountInfo;
import com.test.integrations.domain.seb.BalanceInfo;
import com.test.integrations.domain.seb.RandomIP;
import com.test.integrations.domain.seb.RequestId;
import com.test.integrations.domain.seb.ResourceId;
import com.test.integrations.domain.seb.SebPayments;
import com.test.integrations.domain.seb.SebService;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class SebPaymentFlowService implements SebPayments {
    private final SebService sebService;

    @Override
    public Mono<AccountInfo> getAccountInfo(final RequestId requestId, final RandomIP randomIP) {
        return sebService.getAccountInfo(requestId, randomIP);
    }

    @Override
    public Mono<BalanceInfo> getBalanceInfo(final RequestId requestId, final RandomIP randomIP, final ResourceId resourceId) {
        return sebService.getBalanceInfo(requestId, randomIP, resourceId);
    }
}
