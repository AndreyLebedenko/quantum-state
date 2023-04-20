package org.adinor.core;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.UnaryOperator;
import org.adinor.api.PostRequest;

import java.time.Clock;
import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicLong;

public class State<T> {
  private final AtomicReference<T> innerState;
  private final UnaryOperator<T> updater;
  private final AtomicLong counter;
  private final Instant whenCreated;
  private final Optional<Integer> ttl;

  public State(
      T initialValue,
      Optional<Integer> ttl,
      UnaryOperator<T> updater,
      long maxRequests,
      Instant whenCreated) {
    this.ttl = ttl;
    this.innerState = new AtomicReference<T>(initialValue);
    this.updater = updater;
    this.counter = new AtomicLong(maxRequests);
    this.whenCreated = whenCreated;
  }

  public synchronized Optional<String> produce(Clock clock) throws DataExpiredException {
    if (ttl.map(i -> clock.instant().isAfter(whenCreated.plusSeconds(i))).orElse(false)) {
      throw new DataExpiredException();
    } else if (counter.get() == 0) {
      return Optional.empty();
    } else if (counter.get() > 0) {
      counter.decrementAndGet();
    } // negative - no limit
    return Optional.of(String.valueOf(innerState.updateAndGet(updater)));
  }
}
