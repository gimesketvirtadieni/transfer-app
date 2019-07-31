package org.test.transfer.resources;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import lombok.AllArgsConstructor;
import org.test.transfer.api.TransferRequest;
import org.test.transfer.api.TransferResponse;
import org.test.transfer.db.TransferDAO;

@Path("/api/v1/transfers")
@AllArgsConstructor
public class TransferResource {
    private TransferDAO transferDAO;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public TransferResponse createTransfer(TransferRequest transferRequest) {

        // in 'regular' code there would be some intermediate tier like 'internal service', but for the sake of this exercise - it is OK to use DAO directly
        return transferDAO.createTransfer(transferRequest);
    }
}
