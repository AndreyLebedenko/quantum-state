package org.adinor.api;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.concurrent.ThreadLocalRandom;
import org.immutables.value.Value;

import javax.validation.constraints.Min;
import java.util.Optional;
import org.immutables.value.Value.Default;

@Value.Immutable
@JsonDeserialize(as = ImmutablePostRequest.class)
public abstract class PostRequest {

    public abstract Optional<String> getId();

    @Default
    public String getInitialValue() {
       return String.valueOf(ThreadLocalRandom.current().nextLong(0, Long.MAX_VALUE));
    }

    @Min(0)
    @Default
    public int getStep() {
        return 1;
    }

    @Min(-1)
    @Default
    public long getMaxRequests() {
        return -1;
    }

    @Min(1)
    public abstract Optional<Integer> getTtlSeconds();
}
