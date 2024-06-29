package com.test.integrations.lib.util;

import io.vavr.Tuple2;
import io.vavr.control.Either;
import io.vavr.control.Option;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import static java.util.Objects.requireNonNull;
import java.util.function.Consumer;
import java.util.function.Function;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.OPTIONS;
import static org.springframework.http.HttpMethod.PATCH;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpMethod.PUT;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ClientHttpRequest;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.util.UriBuilder;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

/**
 * Helper class to create request information object that can describe how {@link AbstractWebFluxClient} should perform the request.
 *
 * @param <T> Request body type. Can be {@link Void} if no body should be used.
 * @param <L> Type of the {@link Either.Left}
 * @param <R> Type of the {@link Either.Right}
 */
@Slf4j
public final class WebFluxClientInfo<T, L, R> {
    private final HttpMethod httpMethod;
    private final String uri;
    private final HttpHeaders headers;
    private final MediaType acceptMediaType;
    private final MediaType contentMediaType;
    private final BodyInserter<T, ? super ClientHttpRequest> bodyInserter;
    private final T body;
    private final Function<ClientResponse, Mono<Either<L, R>>> transformer;
    private final Consumer<? super Throwable> onError;
    private final Either<L, R> onErrorReturn;
    private final Option<Tuple2<Class<? extends Throwable>, Either<L, R>>> onSpecificExceptionErrorReturn;
    private final Option<Duration> timeout;

    private final Retry retry;

    private WebFluxClientInfo(final Builder<T, L, R> builder) {
        requireNonNull(builder.httpMethod, "httpMethod cannot be null");
        requireNonNull(builder.uri, "uri cannot be null");

        this.httpMethod = builder.httpMethod;
        this.uri = builder.uri;
        this.headers = builder.headers;
        this.acceptMediaType = builder.acceptMediaType != null ? builder.acceptMediaType : MediaType.ALL;
        this.contentMediaType = builder.contentMediaType;
        this.bodyInserter = builder.bodyInserter != null ? builder.bodyInserter : BodyInserters.empty();
        this.body = builder.body;
        this.transformer = builder.transformer;
        this.onError = builder.onError;
        this.onErrorReturn = builder.onErrorReturn;
        this.onSpecificExceptionErrorReturn = Option.of(builder.onSpecificExceptionErrorReturn);
        this.timeout = Option.of(builder.timeout);
        this.retry = builder.retry;
    }

    /**
     * @param <T> Request body type. Can be {@link Void} if no body should be used.
     * @param <L> Type of the {@link Either.Left}
     * @param <R> Type of the {@link Either.Right}
     */
    public static class Builder<T, L, R> {
        private final HttpMethod httpMethod;
        private final String uri;
        private final HttpHeaders headers = new HttpHeaders();

        private MediaType acceptMediaType;
        private MediaType contentMediaType;
        private T body;
        private BodyInserter<T, ? super ClientHttpRequest> bodyInserter;
        private Function<ClientResponse, Mono<Either<L, R>>> transformer;
        private Consumer<? super Throwable> onError;
        private Either<L, R> onErrorReturn;
        private Tuple2<Class<? extends Throwable>, Either<L, R>> onSpecificExceptionErrorReturn;
        private Duration timeout;

        private Retry retry;

        public Builder(final HttpMethod httpMethod, final String uri) {
            this.httpMethod = httpMethod;
            this.uri = uri;
        }

        public Builder(final HttpMethod httpMethod, final UriBuilder uri) {
            this.httpMethod = httpMethod;
            this.uri = uri.build().toString();
        }

        public Builder<T, L, R> accept(final MediaType acceptMediaType) {
            this.acceptMediaType = acceptMediaType;
            return this;
        }

        public Builder<T, L, R> contentType(final MediaType contentMediaType) {
            this.contentMediaType = contentMediaType;
            return this;
        }

        public Builder<T, L, R> headers(final Map<String, String> headers) {
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setAll(headers);
            return headers(httpHeaders);
        }

        public Builder<T, L, R> headers(final HttpHeaders headers) {
            this.headers.putAll(headers);
            return this;
        }

        public Builder<T, L, R> header(final String key, final String value) {
            this.headers.put(key, List.of(value));
            return this;
        }

        public Builder<T, L, R> body(final BodyInserter<T, ? super ClientHttpRequest> bodyInserter) {
            this.bodyInserter = bodyInserter;
            return this;
        }

        public Builder<T, L, R> body(final T body) {
            this.body = body;
            this.bodyInserter = BodyInserters.fromValue(body);
            return this;
        }

        public Builder<T, L, R> transformer(final Function<ClientResponse, Mono<Either<L, R>>> transformer) {
            this.transformer = transformer;
            return this;
        }

        public Builder<T, L, R> onError(final Consumer<? super Throwable> onError) {
            this.onError = onError;
            return this;
        }

        public Builder<T, L, R> onErrorReturn(final Either<L, R> onErrorReturn) {
            this.onErrorReturn = onErrorReturn;
            return this;
        }



        private void mustBeTrueOrElseThrowIllegalState(final boolean state, final String illegalStateMsg) {
            Option.of(state)
                  .filter(Boolean::booleanValue)
                  .getOrElseThrow(() -> new IllegalStateException(illegalStateMsg));
        }

        public WebFluxClientInfo<T, L, R> build() {
            return new WebFluxClientInfo<>(this);
        }
    }

    public static final class Get<L, R> extends Builder<Void, L, R> {
        public Get(final String uri) {
            super(GET, uri);
        }

        public Get(final UriBuilder uri) {
            super(GET, uri);
        }
    }

    public static final class Options<L, R> extends Builder<Void, L, R> {
        public Options(final String uri) {
            super(OPTIONS, uri);
        }

        public Options(final UriBuilder uri) {
            super(OPTIONS, uri);
        }
    }

    public static final class Post<T, L, R> extends Builder<T, L, R> {
        public Post(final String uri) {
            super(POST, uri);
        }

        public Post(final UriBuilder uri) {
            super(POST, uri);
        }
    }

    public static final class Put<T, L, R> extends Builder<T, L, R> {
        public Put(final String uri) {
            super(PUT, uri);
        }

        public Put(final UriBuilder uri) {
            super(PUT, uri);
        }
    }

    public static final class Patch<T, L, R> extends Builder<T, L, R> {
        public Patch(final String uri) {
            super(PATCH, uri);
        }

        public Patch(final UriBuilder uri) {
            super(PATCH, uri);
        }
    }

    public static final class Delete<L, R> extends Builder<Void, L, R> {
        public Delete(final String uri) {
            super(DELETE, uri);
        }

        public Delete(final UriBuilder uri) {
            super(DELETE, uri);
        }
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public String getUri() {
        return uri;
    }

    public HttpHeaders getHeaders() {
        return headers;
    }

    public MediaType getAcceptMediaType() {
        return acceptMediaType;
    }

    public MediaType getContentMediaType() {
        return contentMediaType;
    }

    public BodyInserter<T, ? super ClientHttpRequest> getBodyInserter() {
        return bodyInserter;
    }

    public Function<ClientResponse, Mono<Either<L, R>>> getTransformer() {
        return transformer;
    }

    public Consumer<? super Throwable> getOnError() {
        return onError;
    }

    public Either<L, R> getOnErrorReturn() {
        return onErrorReturn;
    }

    public Option<Tuple2<Class<? extends Throwable>, Either<L, R>>> getOnSpecificExceptionErrorReturn() {
        return onSpecificExceptionErrorReturn;
    }

    public Option<Duration> getTimeout() {
        return timeout;
    }


    public Option<Retry> getRetry() {
        return Option.of(retry);
    }



}
