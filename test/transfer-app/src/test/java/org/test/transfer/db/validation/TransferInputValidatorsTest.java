package org.test.transfer.db.validation;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.test.transfer.api.TransferRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class TransferInputValidatorsTest {
    private final List<Validator<TransferRequest>> inputValidators = TransferValidators.createInputValidators();

    @Test
    public void testHappyCaseScenario() {

        assertDoesNotThrow(() -> validateRequest(new TransferRequest("LT12100001110100101", "LT12100001110100102", 1, "EUR")));
    }

    @Test
    public void testErrorGivenFromAccountIsMissing() {

        ValidationException resultException = assertThrows(ValidationException.class,
                () -> validateRequest(new TransferRequest(null, "LT12100001110100102", 1, "EUR")));
        assertThat(resultException.getMessage()).contains("Delivering account is not provided");
    }

    @Test
    public void testErrorGivenAmountIsZero() {

        ValidationException resultException = assertThrows(ValidationException.class,
                () -> validateRequest(new TransferRequest("LT12100001110100101", "LT12100001110100102", 0, "EUR")));
        assertThat(resultException.getMessage()).contains("Amount may not be equal to or less than zero");
    }

    // Important note: other Unit Tests are omitted

    protected void validateRequest(TransferRequest transferRequest) {
        inputValidators.forEach(v -> v.validate(transferRequest));
    }
}
