package com.test.integrations.infrastructure;

import com.test.integrations.domain.error.GetPaymentFailedGenericError;
import com.test.integrations.domain.seb.AccountInfo;
import com.test.integrations.domain.seb.AccountItem;
import com.test.integrations.domain.seb.BalanceAmount;
import com.test.integrations.domain.seb.BalanceInfo;
import com.test.integrations.domain.seb.BalanceItem;
import com.test.integrations.domain.seb.BalanceType;
import com.test.integrations.domain.seb.Bban;
import com.test.integrations.domain.seb.Iban;
import com.test.integrations.domain.seb.OwnerName;
import com.test.integrations.domain.seb.RandomIP;
import com.test.integrations.domain.seb.RequestId;
import com.test.integrations.domain.seb.ResourceId;
import com.test.integrations.domain.seb.SebService;
import com.test.integrations.domain.seb.Status;
import com.test.integrations.domain.seb.validation.StringUtil;
import com.test.integrations.lib.domain.ISebReactiveService;
import com.test.integrations.lib.domain.api.GetAccountResponse;
import com.test.integrations.lib.domain.api.GetBalanceResponse;
import io.vavr.Value;
import io.vavr.collection.Seq;
import io.vavr.control.Validation;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;
import static reactor.core.publisher.Mono.error;

@RequiredArgsConstructor
public class SebRemoteService implements SebService {
    private final ISebReactiveService sebReactiveService;

    /**
     * call remote service integration seb for account info , validate and assign to domain classes
     * @param requestId
     * @param randomIP
     */
    @Override
    public Mono<AccountInfo> getAccountInfo(final RequestId requestId, final RandomIP randomIP) {

        return sebReactiveService.getAccounts(requestId.getValue(), randomIP.getValue())
                .flatMap(responses -> responses.fold(
                        errorResponse -> error(new GetPaymentFailedGenericError(
                                "message:[%s] detail:[%s]" .formatted(errorResponse.error, errorResponse.errorMessage))),
                        accountResponse ->
                                validateSebAccountInfo(accountResponse)
                                        .fold(validationError -> error(new GetPaymentFailedGenericError("response validation failed")),
                                                Mono::just))
                );

    }

    /**
     * call remote service integration seb for balance info , validate and assign to domain classes
     * @param requestId
     * @param randomIP
     * @param resourceId
     */
    @Override
    public Mono<BalanceInfo> getBalanceInfo(final RequestId requestId,
                                            final RandomIP randomIP,
                                            final ResourceId resourceId) {
        return sebReactiveService.getBalances(requestId.getValue(), randomIP.getValue(), resourceId.getValue())
                .flatMap(responses -> responses.fold(
                        errorResponse -> error(new GetPaymentFailedGenericError(
                                "message:[%s] detail:[%s]" .formatted(errorResponse.error, errorResponse.errorMessage))),
                        balanceResponse ->
                                validateSebBalanceInfo(balanceResponse)
                                        .fold(validationError -> error(new GetPaymentFailedGenericError("response validation failed")),
                                                Mono::just))
                );
    }


    private Validation<Seq<String>, AccountInfo> validateSebAccountInfo(final GetAccountResponse accountResponse) {

        final var validatedAccountItems = Validation.sequence(accountResponse.getAccountItems()
                        .stream()
                        .map(item -> validateAccountItem(item.getIban(), item.getBban(), item.getStatus(), item.getOwnerName()))
                        .toList())
                .map(Value::toList)
                .mapError(StringUtil.seqStringToString());
        final var validDummy = Iban.validate("test");
        return Validation.combine(validatedAccountItems, validDummy)
                .ap((it, du) -> new AccountInfo(it));
    }

    private Validation<Seq<String>, AccountItem> validateAccountItem(final String iban,
                                                                     final String bban,
                                                                     final String status,
                                                                     final String ownerName) {
        return Validation.combine(
                        Iban.validate(iban),
                        Bban.validate(bban),
                        Status.validate(status),
                        OwnerName.validate(ownerName))
                .ap((ib, bb, st, on) -> new AccountItem(bb, ib, on, st));
    }

    private Validation<Seq<String>, BalanceInfo> validateSebBalanceInfo(final GetBalanceResponse balanceResponse) {
        final var validIban = Iban.validate(balanceResponse.getAccount().getIban());
        final var validatedBalanceItems = Validation.sequence(balanceResponse.getBalances()
                        .stream()
                        .map(item -> validateBalanceItem(item.getBalanceType(), item.getBalanceAmount().getAmount()))
                        .toList())
                .map(Value::toList)
                .mapError(StringUtil.seqStringToString());
        return Validation.combine(validIban, validatedBalanceItems)
                .ap(BalanceInfo::new);
    }

    private Validation<Seq<String>, BalanceItem> validateBalanceItem(final String balanceType,
                                                                     final String balanceAmount) {
        return Validation.combine(
                        BalanceType.validate(balanceType),
                        BalanceAmount.validate(balanceAmount))
                .ap(BalanceItem::new);
    }
}
