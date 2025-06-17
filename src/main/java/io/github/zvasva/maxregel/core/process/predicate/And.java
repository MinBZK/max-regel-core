package io.github.zvasva.maxregel.core.process.predicate;

import io.github.zvasva.maxregel.core.process.AstNode;

import java.util.List;
import java.util.Map;

import static io.github.zvasva.maxregel.core.process.MaxRegelException.requireNonNullArg;

/**
 * Conjunction: combines two predicates and returns true if both predicates are true.
 * @param <T> The type of objects that this predicate tests.
 * @author Arvid Halma
 */
public class And<T, P> extends AbstractPredicate<T, P> {
    private final Predicate<T, P> a, b;

    public And(Predicate<T, P> a, Predicate<T, P> b) {
        this.a = requireNonNullArg(a, "a");
        this.b = requireNonNullArg(b, "b");
    }

    @Override
    public AstNode ast() {
        return new AstNode("and", Map.of(), List.of(a.ast(), b.ast()));
    }

    @Override
    public boolean test(T x) {
        return a.test(x) && b.test(x);
    }

    @Override
    public Predicate<T, P> bind(P parameterData) {
        return new And<>(a.bind(parameterData), b.bind(parameterData));
    }
}
