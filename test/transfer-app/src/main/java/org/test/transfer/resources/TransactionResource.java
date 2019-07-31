package org.test.transfer.resources;

import java.util.List;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import lombok.AllArgsConstructor;
import org.test.transfer.api.Transaction;
import org.test.transfer.db.TransactionDAO;

@Path("/api/v1/transactions")
@AllArgsConstructor
public class TransactionResource {
    private TransactionDAO transactionDAO;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Transaction> getTransfers() {
        return transactionDAO.findAll();
    }

    @Path("{id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Transaction getTransfer(@PathParam("id") long id) {
        return transactionDAO.findById(id)
                .orElseThrow(NotFoundException::new);
    }
}
