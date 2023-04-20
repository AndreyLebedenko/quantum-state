package org.adinor.db;

import org.adinor.api.PostRequest;

import java.util.Optional;
import java.util.function.Supplier;

public interface Storage {

    boolean addRule(String id, PostRequest postRequest);

    Optional<Supplier<Optional<String>>> getValue(String id);
}
