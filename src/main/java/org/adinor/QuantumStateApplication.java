package org.adinor;

import io.dropwizard.Application;
import io.dropwizard.setup.Environment;
import java.time.Clock;
import java.time.ZoneId;
import org.adinor.core.SerialFactory;
import org.adinor.db.InMemoryStorage;
import org.adinor.resources.QuantumGet;
import org.adinor.resources.QuantumPost;

public class QuantumStateApplication extends Application<QuantumStateConfiguration> {

  public static void main(final String[] args) throws Exception {
    new QuantumStateApplication().run(args);
  }

  @Override
  public String getName() {
    return "QuantumState";
  }

  @Override
  public void run(final QuantumStateConfiguration configuration, final Environment environment) {
    InMemoryStorage quantumStorage = new InMemoryStorage(Clock.system(ZoneId.of("UTC")));
    environment.jersey().register(new QuantumPost(quantumStorage, new SerialFactory()));
    environment.jersey().register(new QuantumGet(quantumStorage));
  }
}
