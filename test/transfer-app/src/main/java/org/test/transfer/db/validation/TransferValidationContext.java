package org.test.transfer.db.validation;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.test.transfer.api.Account;
import org.test.transfer.api.AccountBalance;
import org.test.transfer.api.Currency;

@AllArgsConstructor
@EqualsAndHashCode
@ToString(callSuper=true)
public class TransferValidationContext {

    @Getter
    private final Account fromAccount;

    @Getter
    private final AccountBalance fromAccountBalance;

    @Getter
    private final Account toAccount;

    @Getter
    private final AccountBalance toAccountBalance;

    @Getter
    private final double amount;

    @Getter
    private final Currency currency;
}
