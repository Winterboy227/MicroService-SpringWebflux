package com.test.integrations.api.controller.v1;

import com.test.integrations.api.beans.v1.Account;
import com.test.integrations.api.beans.v1.Balance;
import com.test.integrations.api.beans.v1.ErrorResponse;
import com.test.integrations.api.beans.v1.GetAccountResponse;
import com.test.integrations.api.beans.v1.GetBalanceResponse;
import com.test.integrations.domain.seb.AccountInfo;
import com.test.integrations.domain.seb.BalanceInfo;
import com.test.integrations.domain.seb.RandomIP;
import com.test.integrations.domain.seb.RequestId;
import com.test.integrations.domain.seb.ResourceId;
import com.test.integrations.domain.seb.SebPayments;
import io.vavr.Tuple;
import io.vavr.control.Validation;
import static java.util.stream.Collectors.toList;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import static reactor.core.publisher.Mono.just;


@RestController
@RequiredArgsConstructor
public class SebController implements AccountV1 {
    private final SebPayments sebPaymentFlowService;

    @Override
    public Mono<ResponseEntity> getAccounts(final String requestId, final String randomIp) {

        final var validatedRequestId = RequestId.validate(requestId);
        final var validatedRandomIP = RandomIP.validate(randomIp);

        if (validatedRequestId.isInvalid())
            return just(invalidInput(validatedRequestId.getError()));

        if (validatedRandomIP.isInvalid())
            return just(invalidInput(validatedRandomIP.getError()));

        return sebPaymentFlowService.getAccountInfo(validatedRequestId.get(), validatedRandomIP.get())
                .map(response -> ok(mapRetrieveAccountResponse(response)))
                .onErrorResume(e -> just(internalError(e.getMessage())));
    }

    @Override
    public Mono<ResponseEntity> getBalances(final String requestId, final String randomIp, final String resourceId) {

        final var validateInput = Validation.combine(RequestId.validate(requestId),
                        RandomIP.validate(randomIp),
                        ResourceId.validate(resourceId))
                .ap(Tuple::of)
                .mapError(s -> s.mkString(","));
        if (validateInput.isInvalid())
            return just(invalidInput(validateInput.getError()));


        return sebPaymentFlowService.getBalanceInfo(validateInput.get()._1, validateInput.get()._2, validateInput.get()._3)
                .map(response -> ok(mapRetrieveBalanceResponse(response)))
                .onErrorResume(e -> just(internalError(e.getMessage())));
    }


    private static GetAccountResponse mapRetrieveAccountResponse(final AccountInfo accountInfo) {

        return new GetAccountResponse(
                accountInfo.getAccountItems().toJavaStream().map(accountItem -> new Account(
                        accountItem.getIban().getValue(),
                        accountItem.getBban().getValue(),
                        accountItem.getStatus().getValue(),
                        accountItem.getOwnerName().getValue()

                )).collect(toList()));
    }

    private static GetBalanceResponse mapRetrieveBalanceResponse(final BalanceInfo balanceInfo) {

        return new GetBalanceResponse(
                balanceInfo.getIban().getValue(),
                balanceInfo.getBalanceItems().toJavaStream().map(balanceItem -> new Balance(
                        balanceItem.getBalanceType().getValue(),
                        balanceItem.getBalanceAmount().getValue()
                )).collect(toList()));
    }

    public ResponseEntity invalidInput(final String errorMessage) {
        return error(new ErrorResponse(errorMessage, 1111), BAD_REQUEST);
    }

    private ResponseEntity error(final ErrorResponse error,
                                 final HttpStatus status
    ) {
        return new ResponseEntity<>(error, status);
    }

    public <T> ResponseEntity ok(final T OkResponse) {
        return ResponseEntity.ok().body(OkResponse);
    }

    public ResponseEntity internalError(final String errorMessage) {
        return error(new ErrorResponse(errorMessage, 2222), INTERNAL_SERVER_ERROR);
    }
}
