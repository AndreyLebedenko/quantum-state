package org.adinor.api;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonSerialize(as = ImmutableGetResponse.class)
public abstract class GetResponse {
    public abstract long getData();

    public static ImmutableGetResponse.Builder builder() { return ImmutableGetResponse.builder(); }
}
