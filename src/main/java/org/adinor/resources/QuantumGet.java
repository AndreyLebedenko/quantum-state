package org.adinor.resources;

import org.adinor.api.GetResponse;
import org.adinor.db.Storage;
import org.glassfish.jersey.server.ManagedAsync;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.constraints.NotEmpty;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Optional;
import java.util.function.Supplier;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public class QuantumGet {
  private static final Logger logger = LoggerFactory.getLogger(QuantumGet.class);

  private final Storage storage;

  public QuantumGet(final Storage storage) {
    this.storage = storage;
  }

  @GET
  @ManagedAsync
  public void get(@Suspended AsyncResponse asyncResponse, @QueryParam("id") @NotEmpty String id) {
    final Optional<Supplier<Optional<Long>>> supplier = storage.getValue(id);
    final Optional<GetResponse> response =
        supplier.map(Supplier::get).flatMap(d -> d.map(x -> GetResponse.builder().data(x).build()));
    response
        .map(res -> asyncResponse.resume(Response.ok().entity(res).build()))
        .orElseGet(() -> asyncResponse.resume(Response.status(Response.Status.NOT_FOUND).build()));
  }
}
