package org.adinor.core;

import org.adinor.api.PostRequest;

import java.time.Clock;
import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicLong;

public class QuantumState {
  private final AtomicLong innerState;
  private final AtomicLong counter;
  private final int step;
  private final Instant whenCreated;
  private final Optional<Integer> ttl;

  public QuantumState(PostRequest postRequest, Instant whenCreated) {
    this.ttl = postRequest.getTtlSeconds();
    this.innerState =
        new AtomicLong(
            postRequest.getInitialValue().orElseGet(() -> ThreadLocalRandom.current().nextLong()));
    this.step = postRequest.getStep().orElse(0);
    this.counter = new AtomicLong(postRequest.getMaxRequests());
    this.whenCreated = whenCreated;
  }

  public synchronized Optional<Long> produce(Clock clock) throws DataExpiredException {
    if (ttl.map(i -> clock.instant().isAfter(whenCreated.plusSeconds(i))).orElse(false)) {
      throw new DataExpiredException();
    } else if (counter.get() == 0) {
      return Optional.empty();
    } else if (counter.get() > 0) {
      counter.decrementAndGet();
    } // else - no limit
    return Optional.of(innerState.addAndGet(step));
  }
}
