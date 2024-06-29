package com.test.integrations.config;

import com.test.integrations.annotaions.IfShouldUseIntegrations;
import com.test.integrations.lib.config.SebConfiguration;
import com.test.integrations.lib.domain.ISebReactiveService;
import com.test.integrations.lib.util.WebFluxClient;
import java.time.Duration;
import java.util.Map;
import static java.util.Objects.requireNonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@IfShouldUseIntegrations
public class IntegrationConfig {

    @Bean
    public ISebReactiveService sebRestService(@Value("${spring.application.name}") final String requestingSystem,
                                              @Value("${integrations.seb.baseUrl}") final String endpoint,
                                              @Value("${integrations.seb.token}") final String token,
                                              @Value("${integrations.seb.connectTimeout}") final Integer connectTimeout,
                                              @Value("${integrations.seb.readTimeout}") final Integer readTimeout) {

        requireNonNull(endpoint, "seb base url cannot be null");
        requireNonNull(connectTimeout, "seb connection timeout cannot be null");
        requireNonNull(readTimeout, "seb read timeout cannot be null");

        final WebClient webClient = WebFluxClient
                .webClientBuilder(endpoint, connectTimeout)
                .defaultHeaders(Map.of("SYSTEM", requestingSystem))
                .build();

        return SebConfiguration.sebRestService(webClient, token , Duration.ofMillis(readTimeout));
    }
}