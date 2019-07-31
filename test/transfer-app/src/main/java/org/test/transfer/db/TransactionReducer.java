package org.test.transfer.db;

import org.jdbi.v3.core.result.LinkedHashMapRowReducer;
import org.jdbi.v3.core.result.RowView;
import org.test.transfer.api.Account;
import org.test.transfer.api.AccountBalance;
import org.test.transfer.api.Currency;
import org.test.transfer.api.Transaction;
import org.test.transfer.api.TransactionItem;

import java.util.ArrayList;
import java.util.Map;

public class TransactionReducer implements LinkedHashMapRowReducer<Long, Transaction> {

    @Override
    public void accumulate(Map<Long, Transaction> map, RowView rowView) {
        Transaction transaction = map.computeIfAbsent(
                rowView.getColumn("T_ID", Long.class),
                id -> {

                    // getRow will use null's for 'embedded' objects, so initializing account balances separately
                    Transaction t = rowView.getRow(Transaction.class);
                    t.setTransactionItems(new ArrayList<>());
                    return t;
                }
        );

        // creating an account balance as needed
        if (rowView.getColumn("I_ID", Long.class) != null) {

            // getRow will use null's for 'embedded' objects, so initializing Transaction Items separately
            AccountBalance b = rowView.getRow(AccountBalance.class);
            // TODO: figure out how to get currency from CurrencyDAO
            b.setCurrency(rowView.getRow(Currency.class));

            TransactionItem i = rowView.getRow(TransactionItem.class);
            i.setAccountBalance(b);

            transaction.getTransactionItems().add(i);
        }
    }
}
