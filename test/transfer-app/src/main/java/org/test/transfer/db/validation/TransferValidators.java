package org.test.transfer.db.validation;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import org.test.transfer.api.TransferRequest;

@AllArgsConstructor
public class TransferValidators {

    // should be injected
    public static List<Validator<TransferRequest>> createInputValidators() {
        return new ArrayList<>(List.of(

                // this validator is for checking if all attributes are present in the request
                new Validator<>(transferRequest -> {
                    if (transferRequest.getFromAccount() == null) {
                        throw new ValidationException("Delivering account is not provided");
                    }
                    if (transferRequest.getToAccount() == null) {
                        throw new ValidationException("Receiving account is not provided");
                    }
                    if (transferRequest.getCurrencyISOCode() == null) {
                        throw new ValidationException("Currency code is not provided");
                    }
                }),

                // this validator is for checking amount ranges
                new Validator<>(transferRequest -> {
                    if (transferRequest.getAmount() <= 0) {
                        throw new ValidationException("Amount may not be equal to or less than zero");
                    }

                    double maxAmount = 1_000_000_000;
                    if (transferRequest.getAmount() > maxAmount) {
                        throw new ValidationException("Amount may not be greater than " + new DecimalFormat("#").format(maxAmount));
                    }
                }),

                // this validator is for checking amount of decimals
                new Validator<>(transferRequest -> {
                    var value = BigDecimal.valueOf(transferRequest.getAmount());
                    var decimalPart = value.subtract(
                            BigDecimal.valueOf(value.longValue())).toPlainString();

                    if (decimalPart.trim().length() > 4) {
                        throw new ValidationException("Amount may have only two decimal figures");
                    }
                }),

                // this validator is for checking length of currency ISO code
                new Validator<>(transferRequest -> {
                    var isoCode = transferRequest.getCurrencyISOCode();
                    if (isoCode != null && isoCode.length() != 3) {
                        throw new ValidationException("Currency ISO code length must be 3");
                    }
                })
        ));
    }

    // should be injected
    public static List<Validator<TransferValidationContext>> createProcessingValidators() {
        return new ArrayList<>(List.of(

                // this validator is for checking if receiving and delivering accounts are distinct
                new Validator<>(transferValidationContext -> {
                    if (transferValidationContext.getFromAccount().equals(transferValidationContext.getToAccount())) {
                        throw new ValidationException("Delivering account may not be the same as receiving account");
                    }
                }),

                // this validator is for checking if there is sufficient amount in delivering account
                new Validator<>(transferValidationContext -> {
                    if (transferValidationContext.getFromAccountBalance().getCurrentBalance() < transferValidationContext.getAmount()) {
                        throw new ValidationException("Insufficient funds");
                    }
                })
        ));
    }
}
