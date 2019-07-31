package org.test.transfer.db;

import java.util.ArrayList;
import java.util.Map;
import org.jdbi.v3.core.result.LinkedHashMapRowReducer;
import org.jdbi.v3.core.result.RowView;
import org.test.transfer.api.Account;
import org.test.transfer.api.AccountBalance;
import org.test.transfer.api.Currency;

public class AccountReducer implements LinkedHashMapRowReducer<Long, Account> {

    @Override
    public void accumulate(Map<Long, Account> map, RowView rowView) {
        Account account = map.computeIfAbsent(
                rowView.getColumn("A_ID", Long.class),
                id -> {

                    // getRow will use null's for 'embedded' objects, so initializing account balances separately
                    Account a = rowView.getRow(Account.class);
                    a.setAccountBalances(new ArrayList<>());
                    return a;
                }
        );

        // creating an account balance as needed
        if (rowView.getColumn("B_ID", Long.class) != null) {

            // getRow will use null's for 'embedded' objects, so initializing currency separately
            AccountBalance b = rowView.getRow(AccountBalance.class);
            // TODO: figure out how to get currency from CurrencyDAO
            b.setCurrency(rowView.getRow(Currency.class));
            account.getAccountBalances().add(b);
        }
    }
}
