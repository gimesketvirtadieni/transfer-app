package org.test.transfer.db;

import java.util.List;
import java.util.Optional;

import org.jdbi.v3.core.transaction.TransactionIsolationLevel;
import org.jdbi.v3.sqlobject.config.RegisterBeanMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;
import org.jdbi.v3.sqlobject.statement.UseRowReducer;
import org.jdbi.v3.sqlobject.transaction.Transaction;
import org.test.transfer.api.Account;
import org.test.transfer.api.AccountBalance;
import org.test.transfer.api.Currency;

@RegisterBeanMapper(value = Account.class, prefix = "A")
@RegisterBeanMapper(value = AccountBalance.class, prefix = "B")
@RegisterBeanMapper(value = Currency.class, prefix = "C")
public interface AccountDAO {

    String queryBase
            = "SELECT A.ID A_ID, A.NUMBER A_NUMBER, B.ID B_ID, B.CURRENT_BALANCE B_CURRENT_BALANCE, C.ID C_ID, C.ISO_CODE C_ISO_CODE, C.NAME C_NAME "
            + "FROM ACCOUNT A LEFT JOIN ACCOUNT_BALANCE B ON B.ACCOUNT_ID = A.ID LEFT JOIN CURRENCY C ON C.ID = B.CURRENCY_ID";

    @SqlQuery(queryBase)
    @UseRowReducer(AccountReducer.class)
    List<Account> findAll();

    @SqlQuery(queryBase + " WHERE A.ID = :id")
    @UseRowReducer(AccountReducer.class)
    Optional<Account> findById(@Bind("id") long id);

    @SqlQuery(queryBase + " WHERE A.NUMBER = :number FOR UPDATE")
    @UseRowReducer(AccountReducer.class)
    Optional<Account> findByNumberForUpdate(@Bind("number") String number);

    @Transaction(TransactionIsolationLevel.SERIALIZABLE)
    @SqlUpdate("UPDATE ACCOUNT_BALANCE B SET B.CURRENT_BALANCE = :balance WHERE id = :id")
    int updateCurrentBalance(@Bind("id") long id, @Bind("balance") double balance);
}
