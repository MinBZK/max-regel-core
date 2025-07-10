package io.github.zvasva.maxregel.core.process.predicate;

import java.util.Objects;

/**
 * Abstract predicate class that implements equals(), hashcode() and toString(), based on {@link #ast()}.
 *
 * @param <T> the type of object that this predicate tests.
 * @param <B> the type of the predicate's parameter data, which can be bound to the predicate.
 * @author Arvid Halma
 */
public abstract class AbstractPredicate<T, B> implements Predicate<T, B>{


    @Override
    public Predicate<T, B> bind(B parameterData) {
        throw new UnsupportedOperationException("This predicate cannot be bound.");
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof AbstractPredicate<?, ?> that)) return false;
        return Objects.equals(ast(), that.ast());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(ast());
    }

    @Override
    public String toString() {
        return ast().toString();
    }

}
