package org.test.transfer.resources;

import java.util.List;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import lombok.AllArgsConstructor;
import org.test.transfer.api.Currency;
import org.test.transfer.db.CurrencyDAO;

@Path("/api/v1/currencies")
@AllArgsConstructor
public class CurrencyResource {
    private CurrencyDAO currencyDAO;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Currency> getCurrencies() {
        return currencyDAO.findAll();
    }

    @Path("{id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Currency getCurrency(@PathParam("id") long id) {
        return currencyDAO.findById(id)
                .orElseThrow(NotFoundException::new);
    }
}
