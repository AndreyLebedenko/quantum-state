package org.adinor.db;

import java.util.concurrent.atomic.AtomicBoolean;
import org.adinor.api.PostRequest;
import org.adinor.core.DataExpiredException;
import org.adinor.core.State;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Clock;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

public class InMemoryStorage implements Storage {
  private static final Logger logger = LoggerFactory.getLogger(InMemoryStorage.class);

  private final Map<String, State> localStorage;
  private final Clock clock;

  public InMemoryStorage(final Clock clock) {
    this.clock = clock;
    this.localStorage = new ConcurrentHashMap<>();
  }

  @Override
  public boolean addRule(String id, PostRequest postRequest) {
    AtomicBoolean ret = new AtomicBoolean(false);
    localStorage.computeIfAbsent(id, __ -> createNewState(postRequest, ret));
    return ret.get();
  }

  private State createNewState(PostRequest postRequest, AtomicBoolean ret) {
    ret.set(true);
    return new State(postRequest, clock.instant());
  }

  @Override
  public Optional<Supplier<Optional<Long>>> getValue(String id) {
    return Optional.ofNullable(localStorage.get(id))
        .map(
            qs ->
                () -> {
                  try {
                    Optional<Long> ret = qs.produce(clock);
                    if(ret.isPresent()) {
                      return ret;
                    } else {
                      localStorage.remove(id);
                      return ret;
                    }
                  } catch (final DataExpiredException e) {
                    logger.info("Expired: {}", id);
                    localStorage.remove(id);
                    return Optional.empty();
                  }
                });
  }
}
