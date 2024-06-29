package com.test.integrations.lib.infrastructure;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.test.integrations.lib.domain.ISebReactiveService;
import com.test.integrations.lib.domain.api.GetAccountResponse;
import com.test.integrations.lib.domain.api.GetBalanceResponse;
import com.test.integrations.lib.domain.api.SebErrorResponse;
import com.test.integrations.lib.util.AbstractWebFluxClient;
import com.test.integrations.lib.util.WebFluxClientInfo;
import io.vavr.control.Either;
import static io.vavr.control.Either.left;
import io.vavr.control.Try;
import java.time.Duration;
import java.util.function.Function;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
public class SebRestRemote extends AbstractWebFluxClient implements ISebReactiveService {

    private static final String GET_ACCOUNTS_INFO = "/ais/v7/identified2/accounts";
    private static final String GET_ACCOUNT_BALANCE_INFO = "/ais/v7/identified2/accounts/%s/balances";

    private static final ParameterizedTypeReference<GetAccountResponse> GET_ACCOUNT_RESPONSE_TYPE_REFERENCE = new ParameterizedTypeReference<>() {};
    private static final ParameterizedTypeReference<GetBalanceResponse> GET_BALANCE_RESPONSE_TYPE_REFERENCE = new ParameterizedTypeReference<>() {};
    private final Duration readTimeout;
    private final String token;
    public SebRestRemote(final WebClient client, final String token, final Duration readTimeout) {
        super(client);
        this.readTimeout = readTimeout;
        this.token = token;
    }

    /**
     * call seb sandbox endpoint /ais/v7/identified2/accounts and retrieve list of accounts
     * @param requestId
     * @param randomIP
     * @return
     */

    @Override
    public Mono<Either<SebErrorResponse, GetAccountResponse>> getAccounts(final String requestId, final String randomIP) {
        return request(
                new WebFluxClientInfo.Get<SebErrorResponse, GetAccountResponse>(GET_ACCOUNTS_INFO)
                        .accept(APPLICATION_JSON)
                        .contentType(APPLICATION_JSON)
                        .header("X-Request-ID", requestId)
                        .header("PSU-IP-Address", randomIP)
                        .header("Authorization", "Bearer "+ token)
                        .transformer(mapsebResponse(GET_ACCOUNT_RESPONSE_TYPE_REFERENCE))
                        .onErrorReturn(left(new SebErrorResponse("something wrong", "error")))
                        .build()
        );
    }

    /**
     * call seb sandbox endpoint /ais/v7/identified2/accounts/%s/balances and retrieve list of balances for particular resourceId
     * @param requestId
     * @param randomIP
     * @return
     */
    @Override
    public Mono<Either<SebErrorResponse, GetBalanceResponse>> getBalances(final String requestId, final String randomIP, final String resourceId) {

        final var requestUri = String.format(GET_ACCOUNT_BALANCE_INFO, resourceId);
        return request(
                new WebFluxClientInfo.Get<SebErrorResponse, GetBalanceResponse>(requestUri)
                        .accept(APPLICATION_JSON)
                        .contentType(APPLICATION_JSON)
                        .header("X-Request-ID", requestId)
                        .header("PSU-IP-Address", randomIP)
                        .header("Authorization", "Bearer "+ token)
                        .transformer(mapsebResponse(GET_BALANCE_RESPONSE_TYPE_REFERENCE))
                        .onErrorReturn(left(new SebErrorResponse("something wrong", "error")))
                        .build()
        );
    }


    private <T> Function<ClientResponse, Mono<Either<SebErrorResponse, T>>> mapsebResponse(final ParameterizedTypeReference<T> typeReference) {
        return response -> {
            final var respStatusCode = response.statusCode();
            if (respStatusCode.is2xxSuccessful()) {
                return response
                        .bodyToMono(typeReference)
                        .map(body -> {
                            return Either.right(body);
                        });
            }
            return response
                    .bodyToMono(String.class)
                    .map(errorBody -> {
                        if (response.statusCode() == BAD_REQUEST || response.statusCode() == UNAUTHORIZED) {
                            return Either.left(new SebErrorResponse("something wrong with request", "bad request or not authorized"));
                        } else {
                            return Try.of(() -> new ObjectMapper().readValue(errorBody, SebErrorResponse.class))
                                    .<Either<SebErrorResponse, T>>map(Either::left)
                                    .getOrElse(() -> Either.left(new SebErrorResponse("something wrong with request", "error")));
                        }
                    });
        };
    }
}
