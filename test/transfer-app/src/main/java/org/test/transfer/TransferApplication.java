package org.test.transfer;

import java.sql.Connection;
import java.sql.SQLException;

import io.dropwizard.jdbi3.JdbiFactory;
import liquibase.Liquibase;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.jdbi.v3.core.Jdbi;
import org.test.transfer.db.AccountDAO;
import org.test.transfer.db.CurrencyDAO;
import org.test.transfer.db.TransactionDAO;
import org.test.transfer.db.TransferDAO;
import org.test.transfer.health.TransferHealthCheck;
import org.test.transfer.resources.AccountResource;
import org.test.transfer.resources.CurrencyResource;
import org.test.transfer.resources.TransactionResource;
import io.dropwizard.Application;
import io.dropwizard.configuration.EnvironmentVariableSubstitutor;
import io.dropwizard.configuration.SubstitutingSourceProvider;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.db.ManagedDataSource;
import io.dropwizard.lifecycle.Managed;
import io.dropwizard.migrations.MigrationsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.test.transfer.resources.TransferResource;

public class TransferApplication extends Application<TransferConfiguration> {

    // this H2 server is required to allow remote access to the in-memory database (very useful for troubleshooting)
    private org.h2.tools.Server server;

    // delegating main(...) to Dropwizard
    public static void main(String[] args) throws Exception {
        new TransferApplication().run(args);
    }

    @Override
    public String getName() {
        return "transfer-app";
    }

    @Override
    public void initialize(Bootstrap<TransferConfiguration> bootstrap) {

        // enabling variable substitution with environment variables
        bootstrap.setConfigurationSourceProvider(
                new SubstitutingSourceProvider(
                        bootstrap.getConfigurationSourceProvider(),
                        new EnvironmentVariableSubstitutor(false)
                )
        );

        bootstrap.addBundle(new MigrationsBundle<>() {

            @Override
            public DataSourceFactory getDataSourceFactory(TransferConfiguration configuration) {
                return configuration.getDataSourceFactory();
            }
        });
    }

    @Override
    public void run(TransferConfiguration configuration, Environment environment) {

        // creating H2 TCP server to allow remote access to the in-memory database
        environment.lifecycle().manage(new Managed() {

            @Override
            public void start() throws Exception {
                server = org.h2.tools.Server.createTcpServer().start();
            }

            @Override
            public void stop() {
                server.stop();
                server.shutdown();
            }
        });

        // creating database structure AND initial static data (customers, accounts, etc.)
        try (Connection connection = createMigrationDataSource(configuration, environment).getConnection()) {
            Liquibase liquibase = new Liquibase(configuration.getLiquibaseFile(), new ClassLoaderResourceAccessor(), new JdbcConnection(connection));
            liquibase.update("");
        } catch (LiquibaseException | SQLException e) {
            throw new RuntimeException("Error while starting application", e);
        }

        // should be injected in 'regular' code
        JdbiFactory factory = new JdbiFactory();
        Jdbi jdbi = factory.build(environment, configuration.getDataSourceFactory(), "H2");

        CurrencyDAO currencyDAO = jdbi.onDemand(CurrencyDAO.class);
        AccountDAO accountDAO = jdbi.onDemand(AccountDAO.class);
        TransactionDAO transactionDAO = jdbi.onDemand(TransactionDAO.class);
        TransferDAO transferDAO = new TransferDAO(jdbi, currencyDAO, accountDAO, transactionDAO);

        // registering RESTful services
        environment.jersey().register(new CurrencyResource(currencyDAO));
        environment.jersey().register(new AccountResource(accountDAO));
        environment.jersey().register(new TransactionResource(transactionDAO));
        environment.jersey().register(new TransferResource(transferDAO));

        // registering health-check class (which always returns 'healthy' status)
        environment.healthChecks().register("transfer-app", new TransferHealthCheck());
    }

    protected ManagedDataSource createMigrationDataSource(TransferConfiguration configuration, Environment environment) {
        DataSourceFactory dataSourceFactory = configuration.getDataSourceFactory();
        return dataSourceFactory.build(environment.metrics(), "database");
    }
}
