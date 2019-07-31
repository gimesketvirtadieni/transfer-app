package org.test.transfer.db;

import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.transaction.TransactionIsolationLevel;
import org.test.transfer.api.TransferRequest;
import org.test.transfer.api.TransferResponse;
import org.test.transfer.db.validation.TransferValidationContext;
import org.test.transfer.db.validation.TransferValidators;
import org.test.transfer.db.validation.ValidationException;
import org.test.transfer.db.validation.Validator;

@AllArgsConstructor
public class TransferDAO {
    private final Jdbi jdbi;
    private final CurrencyDAO currencyDAO;
    private final AccountDAO accountDAO;
    private final TransactionDAO transactionDAO;

    // should be injected
    private final List<Validator<TransferRequest>> inputValidators = TransferValidators.createInputValidators();

    // should be injected
    private final List<Validator<TransferValidationContext>> processingValidators = TransferValidators.createProcessingValidators();

    public TransferResponse createTransfer(TransferRequest transferRequest) {
        TransferResponse transferResponse = new TransferResponse();

        try {
            if (transferRequest == null) {
                throw new ValidationException("No request data provided");
            }

            // validating input data
            validateRequest(transferRequest);

            // starting a transaction which will be committed by JDBI if registerTransfer does not throw
            jdbi.inTransaction(TransactionIsolationLevel.SERIALIZABLE, handle -> registerTransfer(transferRequest, transferResponse, handle));
        } catch (ValidationException e) {
            transferResponse.setTransactionId(null);
            transferResponse.setError(e.getMessage());
        }

        return transferResponse;
    }

    protected int registerTransfer(TransferRequest transferRequest, TransferResponse transferResponse, Handle handle) {

        // amount of affected records
        int rows = 0;

        // resolving request data into objects
        var amount = transferRequest.getAmount();
        var currency = currencyDAO.findByISOCode(transferRequest.getCurrencyISOCode())
                .orElseThrow(() -> new ValidationException("Invalid currency ISO code provided"));

        var fromAccount = accountDAO.findByNumberForUpdate(transferRequest.getFromAccount())
                .orElseThrow(() -> new ValidationException("Invalid delivering account number provided"));
        var fromAccountBalance = fromAccount.getAccountBalances().stream()
                .filter(b -> currency.equals(b.getCurrency()))
                .findFirst()
                .orElseThrow(() -> new ValidationException("Delivering account does not have any funds in provided currency"));

        var toAccount = accountDAO.findByNumberForUpdate(transferRequest.getToAccount())
                .orElseThrow(() -> new ValidationException("Invalid receiving account number provided"));
        var toAccountBalance = toAccount.getAccountBalances().stream()
                .filter(b -> currency.equals(b.getCurrency()))
                .findFirst()
                .orElseThrow(() -> new ValidationException("Receiving account does not have any funds in provided currency"));

        // validating transfer data before creating transaction
        validateTransfer(new TransferValidationContext(fromAccount, fromAccountBalance, toAccount, toAccountBalance, amount, currency));

        // changing balances
        rows += accountDAO.updateCurrentBalance(fromAccountBalance.getId(), fromAccountBalance.getCurrentBalance() - amount);
        rows += accountDAO.updateCurrentBalance(toAccountBalance.getId(), toAccountBalance.getCurrentBalance() + amount);

        // creating transaction 'master' record
        var transactionId = transactionDAO.createTransaction(LocalDateTime.now());
        rows++;

        // creating transaction item records
        transactionDAO.createTransactionItem(transactionId, fromAccountBalance.getId(), amount, 0);
        transactionDAO.createTransactionItem(transactionId, toAccountBalance.getId(), 0, amount);
        rows += 2;

        if (rows != 5) {
            throw new RuntimeException("Invalid amount of records updated: rows=" + rows);
        }

        // saving transaction in the response object
        transferResponse.setTransactionId(transactionId);

        return rows;
    }

    protected void validateRequest(TransferRequest transferRequest) {
        inputValidators.forEach(v -> v.validate(transferRequest));
    }

    protected void validateTransfer(TransferValidationContext transferValidationContext) {
        processingValidators.forEach(v -> v.validate(transferValidationContext));
    }
}
