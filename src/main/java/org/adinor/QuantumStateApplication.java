package org.adinor;

import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.adinor.core.SerialFactory;
import org.adinor.db.InMemoryQuantumStorage;
import org.adinor.resources.QuantumGet;
import org.adinor.resources.QuantumPost;

import java.time.Clock;
import java.time.ZoneId;

public class QuantumStateApplication extends Application<QuantumStateConfiguration> {

  public static void main(final String[] args) throws Exception {
    new QuantumStateApplication().run(args);
  }

  @Override
  public String getName() {
    return "QuantumState";
  }

  @Override
  public void initialize(final Bootstrap<QuantumStateConfiguration> bootstrap) {
    // TODO: application initialization
  }

  @Override
  public void run(final QuantumStateConfiguration configuration, final Environment environment) {
      InMemoryQuantumStorage quantumStorage = new InMemoryQuantumStorage(Clock.system(ZoneId.of("UTC")));
      environment
        .jersey()
        .register(new QuantumPost(quantumStorage, new SerialFactory()));
    environment
        .jersey()
        .register(new QuantumGet(quantumStorage));
  }
}
