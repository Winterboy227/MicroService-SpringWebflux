package com.test.integrations.controller.v1;

import com.test.integrations.lib.domain.ISebReactiveService;
import com.test.integrations.lib.domain.api.Account;
import com.test.integrations.lib.domain.api.AccountItemData;
import com.test.integrations.lib.domain.api.BalanceAmount;
import com.test.integrations.lib.domain.api.BalanceItemData;
import com.test.integrations.lib.domain.api.GetAccountResponse;
import com.test.integrations.lib.domain.api.GetBalanceResponse;
import com.test.integrations.lib.domain.api.SebErrorResponse;
import static io.vavr.control.Either.left;
import static io.vavr.control.Either.right;
import java.util.List;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import static reactor.core.publisher.Mono.just;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("local")
@AutoConfigureWebTestClient(timeout = "30000")
public class SebControllerTest {

    @Autowired
    private WebTestClient client;
    @MockBean
    private ISebReactiveService sebReactiveService;

    @Test
    public void getAccounts_success_response() {
        final var requestId = "123456";
        final var randomip = "127.0.0.1";

        final var response = new GetAccountResponse(List.of(new AccountItemData("abcd", "SE1550000000054401304039", "54401304039", "arun", "test", "savings", "enabled")));

        Mockito.when(sebReactiveService.getAccounts(eq(requestId), eq(randomip)))
                .thenReturn(just(right(response)));

        client.get()
                .uri(uriBuilder -> uriBuilder.path("/api/rest/v1/accounts")
                        .queryParam("requestId", requestId)
                        .queryParam("randomIp", randomip).build())
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.accountList[0].status").isEqualTo("enabled")
                .jsonPath("$.accountList[0].ownerName").isEqualTo("arun");
    }
    @Test
    public void getAccounts_failure_response() {
        final var requestId = "123456";
        final var randomip = "127.0.0.1";

        final var response = new SebErrorResponse("something wrong", "error");
        Mockito.when(sebReactiveService.getAccounts(eq(requestId), eq(randomip)))
                .thenReturn(just(left(response)));

        client.get()
                .uri(uriBuilder -> uriBuilder.path("/api/rest/v1/accounts")
                        .queryParam("requestId", requestId)
                        .queryParam("randomIp", randomip).build())
                .exchange()
                .expectStatus().is5xxServerError();

    }

    @Test
    public void getbalances_success_response() {
        final var requestId = "123456";
        final var randomip = "127.0.0.1";
        final var resourceId = "123456789";
        final var response = new GetBalanceResponse(new Account("SE0350000000054400047911"), List.of(new BalanceItemData("interimAvailable", new BalanceAmount("500000", "sek"), "true")));
        Mockito.when(sebReactiveService.getBalances(eq(requestId), eq(randomip), eq(resourceId)))
                .thenReturn(just(right(response)));

        client.get()
                .uri(uriBuilder -> uriBuilder.path("/api/rest/v1/accounts/"+ resourceId + "/balances")
                        .queryParam("requestId", requestId)
                        .queryParam("randomIp", randomip).build())
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.iban").isEqualTo("SE0350000000054400047911")
                .jsonPath("$.balanceList[0].balanceType").isEqualTo("interimAvailable")
                .jsonPath("$.balanceList[0].amount").isEqualTo("500000");
    }

}
