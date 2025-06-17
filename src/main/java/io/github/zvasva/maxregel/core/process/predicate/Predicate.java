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
 * @param <B> The object that can be used to initialize this predicate, before {@link #test(Object)} is used.
 *
 * @author Arvid Halma
 */
public interface Predicate<T, B> {

    /**
     * Updates the predicate, given the provided data. Allowing tests based on dynamic data.
     * E.g. the predicate tests is a property is greater than a externally queried value.
     * That queried value can be supplied here, so the test is up-to-date and ready for use.
     * @param parameterData the input that this predicate can use for testing an Object T.
     * @return an immutable instance (this or some derived value)
     */
    Predicate<T, B> bind(B parameterData);

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

    default Predicate<T, B> and(Predicate<T, B> other) {
        return new And<>(this, other);
    }

    default Predicate<T, B> or(Predicate<T, B> other) {
        return new Or<>(this, other);
    }

    default Predicate<T, B> not() {
        return new Not<>(this);
    }

    default java.util.function.Predicate<T> asJavaPredicate() {
        return Predicate.this::test;
    }

}
