package com.test.integrations.lib.domain;

import com.test.integrations.lib.domain.api.GetAccountResponse;
import com.test.integrations.lib.domain.api.GetBalanceResponse;
import com.test.integrations.lib.domain.api.SebErrorResponse;
import reactor.core.publisher.Mono;
import io.vavr.control.Either;

public interface ISebReactiveService {
    Mono<Either<SebErrorResponse, GetAccountResponse>> getAccounts(final String requestId, final String randomIP);

    Mono<Either<SebErrorResponse, GetBalanceResponse>> getBalances(final String requestId, final String randomIP, final String resourceId);
}
