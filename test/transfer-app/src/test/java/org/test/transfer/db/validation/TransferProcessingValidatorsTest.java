package org.test.transfer.db.validation;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.test.transfer.api.Account;
import org.test.transfer.api.AccountBalance;
import org.test.transfer.api.Currency;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class TransferProcessingValidatorsTest {
    private final List<Validator<TransferValidationContext>> processingValidators = TransferValidators.createProcessingValidators();

    @Test
    public void testHappyCaseScenario() {
        var fromAccount = createAccount(1, "LT12100001110100101", 1, "EUR");
        var toAccount = createAccount(2, "LT12100001110100102", 1, "EUR");

        assertDoesNotThrow(() -> validateTransfer(new TransferValidationContext(
                fromAccount,
                fromAccount.getAccountBalances().get(0),
                toAccount,
                toAccount.getAccountBalances().get(0),
                1,
                fromAccount.getAccountBalances().get(0).getCurrency()))
        );
    }

    @Test
    public void testErrorGivenInsufficientFunds() {
        var fromAccount = createAccount(1, "LT12100001110100101", 1, "EUR");
        var toAccount = createAccount(2, "LT12100001110100102", 1, "EUR");

        ValidationException resultException = assertThrows(ValidationException.class,
                () -> validateTransfer(new TransferValidationContext(
                        fromAccount,
                        fromAccount.getAccountBalances().get(0),
                        toAccount,
                        toAccount.getAccountBalances().get(0),
                        1.01d,
                        fromAccount.getAccountBalances().get(0).getCurrency()))
        );
        assertThat(resultException.getMessage()).contains("Insufficient funds");
    }

    // Important note: other Unit Tests are omitted

    protected void validateTransfer(TransferValidationContext transferValidationContext) {
        processingValidators.forEach(v -> v.validate(transferValidationContext));
    }

    protected Account createAccount(long id, String number, double balance, String currency) {
        return new Account(id, number, new ArrayList<>(List.of( new AccountBalance(1, new Currency(1, currency, ""), balance))));
    }
}
