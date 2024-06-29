package com.test.integrations.lib.config;

import com.test.integrations.lib.domain.ISebReactiveService;
import com.test.integrations.lib.infrastructure.SebRestMock;
import com.test.integrations.lib.infrastructure.SebRestRemote;
import java.time.Duration;
import static java.util.Objects.requireNonNull;
import org.springframework.web.reactive.function.client.WebClient;

public class SebConfiguration {
    private final ISebReactiveService service;

    private SebConfiguration(final ISebReactiveService service) {
        this.service = requireNonNull(service, "Service cannot be null");
    }

    public static ISebReactiveService sebMockedService() {
        return new SebRestMock();
    }

    public static ISebReactiveService sebRestService(final WebClient webClient,
                                                     final String token,
                                                     final Duration readTimeout) {
        return new SebRestRemote(webClient, token , readTimeout);
    }

}
