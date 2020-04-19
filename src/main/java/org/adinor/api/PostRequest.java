package org.adinor.api;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

import javax.validation.constraints.Min;
import java.util.Optional;

@Value.Immutable
@JsonDeserialize(as = ImmutablePostRequest.class)
public abstract class PostRequest {
    public abstract Optional<Long> getInitialValue();

    public abstract Optional<Integer> getStep();

    @Min(-1)
    public abstract long getMaxRequests();

    @Min(1)
    public abstract Optional<Integer> getTtlSeconds();
}
