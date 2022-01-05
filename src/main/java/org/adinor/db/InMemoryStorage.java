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

  private final Map<String, State<?>> localStorage;
  private final Clock clock;

  public InMemoryStorage(final Clock clock) {
    this.clock = clock;
    this.localStorage = new ConcurrentHashMap<>();
  }

  @Override
  public boolean addRule(String id, PostRequest postRequest) {
    AtomicBoolean ret = new AtomicBoolean(false);
    try {
      localStorage.computeIfAbsent(id, __ -> createNewLongState(postRequest, ret));
    } catch (NumberFormatException e) {
      localStorage.computeIfAbsent(id, __ -> createNewStringState(postRequest, ret));
    }
    return ret.get();
  }

  private State<Long> createNewLongState(PostRequest postRequest, AtomicBoolean ret) {
    ret.set(true);
    return new State<>(
        Long.parseLong(postRequest.getInitialValue()),
        postRequest.getTtlSeconds(),
        x -> x + postRequest.getStep(),
        postRequest.getMaxRequests(),
        clock.instant());
  }

  private State<String> createNewStringState(PostRequest postRequest, AtomicBoolean ret) {
    ret.set(true);
    return new State<>(
        postRequest.getInitialValue(),
        postRequest.getTtlSeconds(),
        x -> x,
        postRequest.getMaxRequests(),
        clock.instant());
  }

  @Override
  public Optional<Supplier<Optional<String>>> getValue(String id) {
    return Optional.ofNullable(localStorage.get(id))
        .map(
            qs ->
                getObject(id, qs));
  }

  private Supplier<Optional<String>> getObject(String id, State<?> qs) {
    return () -> {
      try {
        Optional<String> ret = qs.produce(clock);
        if (ret.isPresent()) {
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
    };
  }
}
