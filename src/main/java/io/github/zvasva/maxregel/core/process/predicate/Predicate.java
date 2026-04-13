package io.github.zvasva.maxregel.core.process.predicate;

import io.github.zvasva.maxregel.core.process.AstNode;

/**
 * A function that applies a boolean test on an argument.
 * In other words: it maps a value (of type T) to true or false.
 * <p>
 * Why not use {@link java.util.function.Predicate} instead? That is a pure function
 * and can't do much else than being applied. This version allows for mapping to an
 * abstract syntax tree (AST).
 *
 * @param <T> The value type it supports to apply a test on.
 *
 * @author Arvid Halma
 */
public interface Predicate<T> {

    /**
     * Evaluates this predicate on the given argument.
     * @param arg the input argument
     * @return true if the input argument matches the predicate, otherwise false
     */
    boolean test(T arg);

    /**
     * Predicate as abstract syntax tree.
     * @return s-expression
     */
    AstNode ast();

    default Predicate<T> and(Predicate<T> other) {
        return new And<>(this, other);
    }

    default Predicate<T> or(Predicate<T> other) {
        return new Or<>(this, other);
    }

    default Predicate<T> not() {
        return new Not<>(this);
    }

    default java.util.function.Predicate<T> asJavaPredicate() {
        return Predicate.this::test;
    }

}
