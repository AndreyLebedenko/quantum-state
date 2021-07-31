package org.adinor.resources;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import org.adinor.api.ImmutablePostRequest;
import org.adinor.api.PostRequest;
import org.adinor.api.PostResponse;
import org.adinor.core.SerialFactory;
import org.adinor.db.Storage;
import org.glassfish.jersey.server.ManagedAsync;

@Path("/")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class QuantumPost {

  private final Storage storage;
  private final SerialFactory serialFactory;

  public QuantumPost(Storage storage, SerialFactory serialFactory) {
    this.storage = storage;
    this.serialFactory = serialFactory;
  }

  @POST
  @ManagedAsync
  @Produces("application/json")
  public void add(@Suspended AsyncResponse asyncResponse, PostRequest postRequest) {
    if (postRequest == null) postRequest = ImmutablePostRequest.builder().build();
    final String id = postRequest.getId().orElseGet(() -> serialFactory.produce());
    if (storage.addRule(id, postRequest)) {
      asyncResponse.resume(Response.ok().entity(PostResponse.builder().id(id).build()).build());
    } else {
      asyncResponse.resume(Response.status(Status.CONFLICT).build());
    }
  }
}
