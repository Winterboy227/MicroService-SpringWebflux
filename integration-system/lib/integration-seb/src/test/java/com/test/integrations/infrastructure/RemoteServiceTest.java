package com.test.integrations.infrastructure;

import com.test.integrations.lib.config.SebConfiguration;
import com.test.integrations.lib.domain.ISebReactiveService;
import com.test.integrations.lib.util.WebFluxClient;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Duration;
import org.junit.AfterClass;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockserver.integration.ClientAndServer;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

public class RemoteServiceTest {
    private static final String REQUEST_ID = "123456";
    private static final String RANDOM_IP = "127.0.0.1";
    private static final String RESOURCE_ID = "5678";
    private static ClientAndServer mockWebServer;
    private static ISebReactiveService remoteService;
    private static final int port = 8080;
    @BeforeClass
    public static void setupClass() {

        final WebClient webClient = WebFluxClient.webClientBuilder("http://localhost:" + port, 5000).build();
        remoteService = SebConfiguration.sebRestService(webClient, "1234", Duration.ofMillis(5000));
        mockWebServer = ClientAndServer.startClientAndServer(port);
    }

    @Before
    public void setup() {
        mockWebServer.reset();
    }

    @AfterClass
    public static void tearDownClass() {
        mockWebServer.stop();
    }

    @Test
    public void getAccounts_ok_response() throws IOException {

        mockWebServer
                .when(
                        request()
                                .withMethod("GET")
                                .withPath("/ais/v7/identified2/accounts")
                )
                .respond(
                        response()
                                .withStatusCode(200)
                                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                                .withBody(readFileOnClasspath("accounts/accounts_response.json"))
                );

        final var result = remoteService.getAccounts(REQUEST_ID, RANDOM_IP).block();
        assertNotNull(result);
        assertTrue(result.isRight());
    }


    @Test
    public void getAccounts_failure_response() throws IOException {

        mockWebServer
                .when(
                        request()
                                .withMethod("GET")
                                .withPath("/ais/v7/identified2/accounts")
                )
                .respond(
                        response()
                                .withStatusCode(500)
                                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                                .withBody(readFileOnClasspath("accounts/accounts_response_failure.json"))
                );

        final var result = remoteService.getAccounts(REQUEST_ID, RANDOM_IP).block();
        assertNotNull(result);
        assertTrue(result.isLeft());
    }


    @Test
    public void getBalances_ok_response() throws IOException {

        mockWebServer
                .when(
                        request()
                                .withMethod("GET")
                                .withPath("/ais/v7/identified2/accounts/5678/balances")
                )
                .respond(
                        response()
                                .withStatusCode(200)
                                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                                .withBody(readFileOnClasspath("accounts/balances_response.json"))
                );

        final var result = remoteService.getBalances(REQUEST_ID, RANDOM_IP, RESOURCE_ID).block();
        assertNotNull(result);
        assertTrue(result.isRight());
    }

    public String readFileOnClasspath(final String path) throws IOException {
        return Files.readString(new ClassPathResource(path).getFile().toPath());
    }

}
