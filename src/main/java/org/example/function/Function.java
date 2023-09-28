package org.example.function;

import java.util.Optional;

public interface Function<R, A> {
    Optional<Optional<R>> compute(A arg);
}
