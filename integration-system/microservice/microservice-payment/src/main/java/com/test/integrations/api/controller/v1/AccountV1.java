package com.test.integrations.api.controller.v1;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import reactor.core.publisher.Mono;

public interface AccountV1 {

    String BASE_V1 = "/api/rest/v1";
    String GET_ACCOUNTS = BASE_V1 + "/accounts";
    String GET_BALANCES = BASE_V1 + "/accounts/{resourceId}/balances";

    /**
     *  Api to retrieve the account info from integration-seb lib service
     * @param requestId
     * @param randomIp
     * @return
     */
    @GetMapping(GET_ACCOUNTS)
    Mono<ResponseEntity> getAccounts(@RequestParam(value = "requestId", required = false) final String requestId,
                                     @RequestParam(value = "randomIp", required = false) final String randomIp);

    /**
     * Api to retrieve the balance info on resource id from integration-seb lib service
     * @param requestId
     * @param randomIp
     * @param resourceId
     * @return
     */
    @GetMapping(GET_BALANCES)
    Mono<ResponseEntity> getBalances(@RequestParam(value = "requestId", required = false) final String requestId,
                                     @RequestParam(value = "randomIp", required = false) final String randomIp,
                                     @PathVariable("resourceId") final String resourceId);
}
