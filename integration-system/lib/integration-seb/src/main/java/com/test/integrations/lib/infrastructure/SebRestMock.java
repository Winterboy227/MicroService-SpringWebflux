package com.test.integrations.lib.infrastructure;

import com.test.integrations.lib.domain.ISebReactiveService;
import com.test.integrations.lib.domain.api.Account;
import com.test.integrations.lib.domain.api.AccountItemData;
import com.test.integrations.lib.domain.api.BalanceAmount;
import com.test.integrations.lib.domain.api.BalanceItemData;
import com.test.integrations.lib.domain.api.GetAccountResponse;
import com.test.integrations.lib.domain.api.GetBalanceResponse;
import com.test.integrations.lib.domain.api.SebErrorResponse;
import io.vavr.control.Either;
import java.util.List;
import reactor.core.publisher.Mono;

public class SebRestMock implements ISebReactiveService {
    @Override
    public Mono<Either<SebErrorResponse, GetAccountResponse>> getAccounts(final String requestId, final String randomIP) {
        return Mono.just(Either.right(new GetAccountResponse(List.of(new AccountItemData("5a59028c-e757-4f22-b88c-3ba90573383c",
                        "SE3750000000054400047881",
                        "54400047881",
                        "FREDDIE GUMMESSON",
                        "Privatkonto",
                        "Private",
                        "enabled"),
                new AccountItemData("131232435435",
                        "SE375000000005440004981",
                        "54400044981",
                        "MAX PAYNE",
                        "Privatkonto",
                        "Private",
                        "disabled")))));
    }

    @Override
    public Mono<Either<SebErrorResponse, GetBalanceResponse>> getBalances(final String requestId, final String randomIPfinal, String resourceId) {
        return Mono.just(Either.right(new GetBalanceResponse(new Account("123456"),
                List.of(new BalanceItemData("interimAvailable", new BalanceAmount("10", "sek"), "true")))));
    }
}
