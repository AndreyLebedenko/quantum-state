package org.adinor.resources;

import org.adinor.api.PostRequest;
import org.adinor.api.PostResponse;
import org.adinor.core.SerialFactory;
import org.adinor.db.QuantumStorage;
import org.glassfish.jersey.server.ManagedAsync;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.UUID;

@Path("/")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class QuantumPost {

  private QuantumStorage quantumStorage;
  private SerialFactory serialFactory;

  public QuantumPost(final QuantumStorage quantumStorage, final SerialFactory serialFactory) {
    this.quantumStorage = quantumStorage;
    this.serialFactory = serialFactory;
  }

  @POST
  @ManagedAsync
  public PostResponse add(final PostRequest postRequest) {
    String id = serialFactory.produce();
    if (quantumStorage.addRule(id, postRequest)) {
      return PostResponse.builder().id(id).build();
    } else throw new RuntimeException("Can not execute");
  }
}
