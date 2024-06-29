package com.test.integrations.lib.util;

import io.netty.channel.ChannelOption;
import io.netty.channel.epoll.EpollChannelOption;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.vavr.control.Option;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import static java.util.Objects.requireNonNull;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.util.Assert;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.transport.ProxyProvider;

/**
 * WebFluxClient that can be used to call web services.
 */
public final class WebFluxClient {

    private final WebClient client;

    private static final Integer DEFAULT_TCP_KEEPIDLE = 30;
    private static final Integer DEFAULT_TCP_KEEPINTVL = 30;
    private static final Integer DEFAULT_TCP_KEEPCNT = 2;

    public WebFluxClient(final WebClient client, final Class<?> loggingClass) {
        requireNonNull(client, "client cannot be null");
        this.client = client.mutate()
                       .build();
    }


    public static WebClientBuilder webClientBuilder(final String baseUrl, final Integer connectTimeoutMillis) {
        return new WebClientBuilder(baseUrl, connectTimeoutMillis);
    }

    public static class WebClientBuilder {
        private final String baseUrl;
        private final Integer connectTimeoutMillis;
        private Integer readTimeoutMillis = 60000;
        private Map<String, String> defaultHeaders;
        private Integer maxByteInMemorySize;
        private SslContext sslContext;
        private InetSocketAddress proxyAddress;
        private Boolean tcpKeepAlive = false;
        private Boolean enableConnectionPool = true;
        private List<ExchangeFilterFunction> filters = List.of();

        private WebClientBuilder(String baseUrl, Integer connectTimeoutMillis) {
            this.baseUrl = baseUrl;
            this.connectTimeoutMillis = connectTimeoutMillis;
        }

        public WebClientBuilder defaultHeaders(Map<String, String> defaultHeaders) {
            requireNonNull(defaultHeaders, "defaultHeaders cannot be null");
            this.defaultHeaders = defaultHeaders;
            return this;
        }

        public WebClientBuilder maxByteInMemorySize(Integer maxByteInMemorySize) {
            requireNonNull(maxByteInMemorySize, "maxByteInMemorySize cannot be null");
            Assert.isTrue(maxByteInMemorySize > 0, "maxByteInMemorySize has to be greater then 0");
            this.maxByteInMemorySize = maxByteInMemorySize;
            return this;
        }

        public WebClientBuilder sslContext(SslContext sslContext) {
            requireNonNull(sslContext, "sslContext cannot be null");
            this.sslContext = sslContext;
            return this;
        }

        public WebClientBuilder proxyAddress(InetSocketAddress proxyAddress) {
            requireNonNull(proxyAddress, "proxyAddress cannot be null");
            this.proxyAddress = proxyAddress;
            return this;
        }

        public WebClientBuilder tcpKeepAlive(Boolean tcpKeepAlive) {
            requireNonNull(tcpKeepAlive, "tcpKeepAlive cannot be null");
            this.tcpKeepAlive = tcpKeepAlive;
            return this;
        }

        public WebClientBuilder readTimeoutMillis(Integer readTimeoutMillis) {
            requireNonNull(readTimeoutMillis, "readTimeoutMillis cannot be null");
            this.readTimeoutMillis = readTimeoutMillis;
            return this;
        }


        public WebClientBuilder enableConnectionPool(Boolean enableConnectionPool) {
            requireNonNull(enableConnectionPool, "enableConnectionPool cannot be null");
            this.enableConnectionPool = enableConnectionPool;
            return this;
        }

        public WebClientBuilder filters(final List<ExchangeFilterFunction> filters) {
            requireNonNull(filters, "filters cannot be null");
            this.filters = filters;
            return this;
        }


        public WebClient build() {
            defaultHeaders = Option.of(defaultHeaders).getOrElse(Map.of());
            return buildWebClient();
        }

        private WebClient buildWebClient() {

            final WebClient webClient = WebClient.builder()
                    .baseUrl(baseUrl)
                    .apply(builder -> filters.forEach(builder::filter))
                    .clientConnector(new ReactorClientHttpConnector(httpClient()))
                    .defaultHeaders(httpHeaders -> httpHeaders.setAll(defaultHeaders))
                    .build();

            return Option.of(maxByteInMemorySize).fold(
                    () -> webClient,
                    maxInMemorySize -> webClient.mutate()
                            .exchangeStrategies(ExchangeStrategies.builder()
                                    .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(maxInMemorySize))
                                    .build())
                            .build()
            );
        }

        private HttpClient httpClient() {
            HttpClient client;

            if(enableConnectionPool)
                client = pooledHttpClient();
            else
                client = nonPooledHttpClient();

            return Option.of(sslContext).fold(
                    () -> client,
                    ctx -> client.secure(sslContextSpec -> sslContextSpec.sslContext(ctx))
            );
        }

        private HttpClient pooledHttpClient() {
            return httpClientWithProxyAndReadTimeout(HttpClient.create()
                    .compress(true)
                    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectTimeoutMillis)
                    .option(EpollChannelOption.TCP_KEEPIDLE, DEFAULT_TCP_KEEPIDLE)
                    .option(EpollChannelOption.TCP_KEEPINTVL, DEFAULT_TCP_KEEPINTVL)
                    .option(EpollChannelOption.TCP_KEEPCNT, DEFAULT_TCP_KEEPCNT)
                    .keepAlive(tcpKeepAlive));
        }

        private HttpClient nonPooledHttpClient() {
            return httpClientWithProxyAndReadTimeout(HttpClient.newConnection().responseTimeout(
                    Duration.ofMillis(readTimeoutMillis)).option(
                    ChannelOption.CONNECT_TIMEOUT_MILLIS, connectTimeoutMillis));
        }

        private HttpClient httpClientWithProxyAndReadTimeout(final HttpClient httpClient) {
            return setProxyIfDefined(httpClient).doOnConnected(conn -> conn.addHandlerLast(new ReadTimeoutHandler(readTimeoutMillis/1000)));
        }

        private HttpClient setProxyIfDefined(final HttpClient httpClient) {
            return proxyAddress == null ? httpClient : httpClient.proxy(proxy -> proxy.type(ProxyProvider.Proxy.HTTP).address(proxyAddress));
        }
    }
}
