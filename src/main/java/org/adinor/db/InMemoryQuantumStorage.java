package org.adinor.db;

import org.adinor.api.PostRequest;
import org.adinor.core.DataExpiredException;
import org.adinor.core.QuantumState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Clock;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

public class InMemoryQuantumStorage implements QuantumStorage {
  private static final Logger logger = LoggerFactory.getLogger(InMemoryQuantumStorage.class);

  private final Map<String, QuantumState> localStorage;
  private final Clock clock;

  public InMemoryQuantumStorage(final Clock clock) {
    this.clock = clock;
    this.localStorage = new ConcurrentHashMap<>();
  }

  @Override
  public boolean addRule(String id, PostRequest postRequest) {
    localStorage.computeIfAbsent(id, __ -> new QuantumState(postRequest, clock.instant()));
    return true;
  }

  @Override
  public Optional<Supplier<Optional<Long>>> getValue(String id) {
    return Optional.ofNullable(localStorage.get(id))
        .map(
            qs ->
                () -> {
                  try {
                    return qs.produce(clock);
                  } catch (final DataExpiredException e) {
                    logger.info("Expired: {}", id);
                    localStorage.remove(id);
                    return Optional.empty();
                  }
                });
  }
}
