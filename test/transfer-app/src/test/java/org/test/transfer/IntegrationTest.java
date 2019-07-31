package org.test.transfer;

import io.dropwizard.testing.ResourceHelpers;
import io.dropwizard.testing.junit5.DropwizardAppExtension;
import io.dropwizard.testing.junit5.DropwizardExtensionsSupport;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.test.transfer.api.Transaction;
import org.test.transfer.api.TransferRequest;
import org.test.transfer.api.TransferResponse;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(DropwizardExtensionsSupport.class)
public class IntegrationTest {

    private static final String CONFIG_PATH = ResourceHelpers.resourceFilePath("transfer-app-test.yml");
    private static final DropwizardAppExtension<TransferConfiguration> RULE = new DropwizardAppExtension<>(TransferApplication.class, CONFIG_PATH);

    @BeforeAll
    public static void migrateDb() throws Exception {
        RULE.getApplication().run("db", "migrate", CONFIG_PATH);

        RULE.getConfiguration();
        RULE.getEnvironment();
    }

    // Important note: integration tests rely on static data provided in migrations.xml file; this approach is not suitable in real-life scenario
    @Test
    public void testPostTransferHappyCaseScenario() {

        // posting transfer request
        var transferRequest = new TransferRequest("LT12100001110100101", "LT12100001110100102", 1, "EUR");
        TransferResponse transferResponse = postTransfer(transferRequest);

        // validating response
        assertThat(transferResponse.getTransactionId()).isNotNull();
        assertThat(transferResponse.getError()).isNull();

        // extracting transaction details
        var transaction = getTransaction(transferResponse.getTransactionId());

        // validating transaction details
        var localDateTime = LocalDateTime.now();
        assertThat(transaction.getRegistrationDate().getYear()).isEqualTo(localDateTime.getYear());
        assertThat(transaction.getRegistrationDate().getMonth()).isEqualTo(localDateTime.getMonth());
        assertThat(transaction.getRegistrationDate().getDayOfMonth()).isEqualTo(localDateTime.getDayOfMonth());
        assertThat(transaction.getTransactionItems().size()).isEqualTo(2);

        var transactionItem1 = transaction.getTransactionItems().get(0);
        var transactionItem2 = transaction.getTransactionItems().get(1);
        assertThat(transactionItem1.getDebitAmount() + transactionItem1.getCreditAmount()).isEqualTo(transferRequest.getAmount());
        assertThat(transactionItem2.getDebitAmount() + transactionItem2.getCreditAmount()).isEqualTo(transferRequest.getAmount());
        assertThat(transactionItem1.getDebitAmount()).isEqualTo(transactionItem2.getCreditAmount());

        var accountBalance1 = transactionItem1.getAccountBalance();
        var accountBalance2 = transactionItem2.getAccountBalance();
        assertThat(accountBalance1.getCurrency().getIsoCode()).isEqualTo("EUR");
        assertThat(accountBalance2.getCurrency().getIsoCode()).isEqualTo("EUR");
        assertThat(accountBalance1.getCurrentBalance()).isEqualTo(99.01d);
        assertThat(accountBalance2.getCurrentBalance()).isEqualTo(201.02d);
    }

    protected Transaction getTransaction(long transactionId) {
        return RULE.client().target("http://localhost:" + RULE.getLocalPort() + "/api/v1/transactions/" + transactionId)
                .request()
                .get()
                .readEntity(Transaction.class);
    }

    protected TransferResponse postTransfer(TransferRequest transferRequest) {
        return RULE.client().target("http://localhost:" + RULE.getLocalPort() + "/api/v1/transfers")
                .request()
                .post(Entity.entity(transferRequest, MediaType.APPLICATION_JSON_TYPE))
                .readEntity(TransferResponse.class);
    }
}
