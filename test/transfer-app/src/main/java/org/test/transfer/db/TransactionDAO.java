package org.test.transfer.db;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.jdbi.v3.core.transaction.TransactionIsolationLevel;
import org.jdbi.v3.sqlobject.config.RegisterBeanMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;
import org.jdbi.v3.sqlobject.statement.UseRowReducer;
import org.test.transfer.api.AccountBalance;
import org.test.transfer.api.Currency;
import org.test.transfer.api.Transaction;
import org.test.transfer.api.TransactionItem;

@RegisterBeanMapper(value = Transaction.class, prefix = "T")
@RegisterBeanMapper(value = TransactionItem.class, prefix = "I")
@RegisterBeanMapper(value = AccountBalance.class, prefix = "B")
@RegisterBeanMapper(value = Currency.class, prefix = "C")
public interface TransactionDAO {

    String queryBase
            = "SELECT T.ID T_ID, T.REGISTRATION_DATE T_REGISTRATION_DATE, I.ID I_ID, I.ACCOUNT_BALANCE_ID I_ACCOUNT_BALANCE_ID, I.CREDIT_AMOUNT I_CREDIT_AMOUNT, I.DEBIT_AMOUNT I_DEBIT_AMOUNT, B.ID B_ID, B.CURRENCY_ID B_CURRENCY_ID, B.CURRENT_BALANCE B_CURRENT_BALANCE, C.ID C_ID, C.ISO_CODE C_ISO_CODE, C.NAME C_NAME "
            + "FROM TRANSACTION T LEFT JOIN TRANSACTION_ITEM I ON I.TRANSACTION_ID = T.ID LEFT JOIN ACCOUNT_BALANCE B ON B.ID = I.ACCOUNT_BALANCE_ID LEFT JOIN CURRENCY C ON C.ID = B.CURRENCY_ID";

    @SqlQuery(queryBase)
    @UseRowReducer(TransactionReducer.class)
    List<Transaction> findAll();

    @SqlQuery(queryBase + " WHERE T.ID = :id")
    @UseRowReducer(TransactionReducer.class)
    Optional<Transaction> findById(@Bind("id") long id);

    @org.jdbi.v3.sqlobject.transaction.Transaction(TransactionIsolationLevel.SERIALIZABLE)
    @SqlUpdate("INSERT INTO TRANSACTION(REGISTRATION_DATE) VALUES (:registrationDate)")
    @GetGeneratedKeys
    long createTransaction(@Bind("registrationDate") LocalDateTime registrationDate);

    @org.jdbi.v3.sqlobject.transaction.Transaction(TransactionIsolationLevel.SERIALIZABLE)
    @SqlUpdate("INSERT INTO TRANSACTION_ITEM(TRANSACTION_ID, ACCOUNT_BALANCE_ID, DEBIT_AMOUNT, CREDIT_AMOUNT) VALUES (:transactionId, :accountBalanceId, :debitAmount, :creditAmount)")
    @GetGeneratedKeys
    long createTransactionItem(@Bind("transactionId") long transactionId, @Bind("accountBalanceId") long accountBalanceId, @Bind("debitAmount") double debitAmount, @Bind("creditAmount") double creditAmount);
}
