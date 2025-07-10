package io.github.zvasva.maxregel.core.process.predicate;

import io.github.zvasva.maxregel.core.process.AstNode;

import java.util.List;
import java.util.Map;

import static io.github.zvasva.maxregel.core.process.MaxRegelException.requireNonNullArg;

/**
 * Disjunction: combines two predicates and returns true if at least one of the predicates is true.
 *
 * @param <T> The type of objects that this predicate tests.
 * @param <B> The type of the predicate's parameter data, which can be bound to the predicate.
 * @author Arvid Halma
 */
public class Or<T, B> extends AbstractPredicate<T, B> {
    private final Predicate<T, B> a, b;

    public Or(Predicate<T, B> a, Predicate<T, B> b) {
        this.a = requireNonNullArg(a, "a");
        this.b = requireNonNullArg(b, "b");
    }

    @Override
    public AstNode ast() {
        return new AstNode("or", Map.of(), List.of(a.ast(), b.ast()));
    }

    @Override
    public boolean test(T x) {
        return a.test(x) || b.test(x);
    }


    @Override
    public Predicate<T, B> bind(B parameterData) {
        return new Or<>(a.bind(parameterData), b.bind(parameterData));
    }
}
