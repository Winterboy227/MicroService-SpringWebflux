package com.test.integrations.lib.util;

import io.vavr.control.Either;
import java.time.Duration;
import static java.time.temporal.ChronoUnit.SECONDS;
import static java.util.Objects.requireNonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;


/**
 * WebFluxClient that can be used to call web services .
 */
public abstract class AbstractWebFluxClient {
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    protected final WebClient client;


    public AbstractWebFluxClient(final WebClient client) {
        requireNonNull(client, "client cannot be null");
        this.client = client;
    }

    public <T, L, R> Mono<Either<L, R>> request(final WebFluxClientInfo<T, L, R> info) {
        return request(info, client);
    }


    protected <T, L, R> Mono<Either<L, R>> request(final WebFluxClientInfo<T, L, R> info, WebClient client) {
        return Mono.deferContextual(ctx -> {
            requireNonNull(info, "info cannot be null");
            HttpHeaders headers = info.getHeaders();


            var requestSpec = client.method(info.getHttpMethod())
                    .uri(info.getUri())
                    .accept(info.getAcceptMediaType())
                    .contentType(info.getContentMediaType())
                    .headers(h -> h.addAll(headers))
                    .body(info.getBodyInserter())
                    .exchangeToMono(response -> {
                        return info.getTransformer().apply(response);
                    })
                    .timeout(info.getTimeout().getOrElse(Duration.of(20, SECONDS)));

            if (info.getRetry().isDefined()) {
                requestSpec = requestSpec.retryWhen(info.getRetry().get());
            }

            if(info.getOnError() != null){
                requestSpec = requestSpec.doOnError(info.getOnError());
            }

            final boolean onSpecificExceptionErrorExits = info.getOnSpecificExceptionErrorReturn()
                    .exists(tuple -> tuple._1() != null && tuple._2() != null);

            if(onSpecificExceptionErrorExits){
                requestSpec = requestSpec.onErrorReturn(
                        info.getOnSpecificExceptionErrorReturn().get()._1(),
                        info.getOnSpecificExceptionErrorReturn().get()._2()
                );
            }

            if(info.getOnErrorReturn() != null){
                requestSpec = requestSpec.onErrorReturn(info.getOnErrorReturn());
            }
            return requestSpec;
        });
    }

}
