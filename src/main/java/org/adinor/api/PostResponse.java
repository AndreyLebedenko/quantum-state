package org.adinor.api;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonSerialize(as = ImmutablePostResponse.class)
public abstract class PostResponse {

    public abstract String getId();

    @JsonIgnore
    public static ImmutablePostResponse.Builder builder() { return ImmutablePostResponse.builder(); }
}
