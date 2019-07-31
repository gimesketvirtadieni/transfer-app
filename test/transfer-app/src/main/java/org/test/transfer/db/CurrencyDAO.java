package org.test.transfer.db;

import org.jdbi.v3.sqlobject.config.RegisterBeanMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.test.transfer.api.Currency;

import java.util.List;
import java.util.Optional;

@RegisterBeanMapper(Currency.class)
public interface CurrencyDAO {

    String queryBase = "SELECT ID, ISO_CODE, NAME FROM CURRENCY C";

    @SqlQuery(queryBase)
    List<Currency> findAll();

    @SqlQuery(queryBase + " WHERE C.ID = :id")
    Optional<Currency> findById(@Bind("id") long id);

    @SqlQuery(queryBase + " WHERE C.ISO_CODE = :isoCode")
    Optional<Currency> findByISOCode(@Bind("isoCode") String isoCode);
}
