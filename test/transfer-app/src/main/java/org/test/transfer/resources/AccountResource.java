package org.test.transfer.resources;

import java.util.List;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import lombok.AllArgsConstructor;
import org.test.transfer.api.Account;
import org.test.transfer.db.AccountDAO;

@Path("/api/v1/accounts")
@AllArgsConstructor
public class AccountResource {
    private AccountDAO accountDAO;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Account> getAccounts() {
        return accountDAO.findAll();
    }

    @Path("{id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Account getAccount(@PathParam("id") long id) {
        return accountDAO.findById(id)
                .orElseThrow(NotFoundException::new);
    }
}
