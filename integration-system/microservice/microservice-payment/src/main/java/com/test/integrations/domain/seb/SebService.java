package com.test.integrations.domain.seb;

import reactor.core.publisher.Mono;

public interface SebService {

    Mono<AccountInfo> getAccountInfo(final RequestId requestId, final RandomIP randomIP);
    Mono<BalanceInfo> getBalanceInfo(final RequestId requestId, final RandomIP randomIP, final ResourceId resourceId);

}
