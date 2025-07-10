package io.github.zvasva.maxregel.core.process.predicate;

import io.github.zvasva.maxregel.core.process.AstNode;

import java.util.List;
import java.util.Map;

import static io.github.zvasva.maxregel.core.process.MaxRegelException.requireNonNullArg;

/**
 * A predicate that wraps another predicate and negates its result.
 *
 * @param <T> The type of objects that this predicate tests.
 * @param <B> The type of the predicate's parameter data, which can be bound to the predicate.
 * @author Arvid Halma
 */
public class Not<T, B> extends AbstractPredicate<T, B> {
    private final Predicate<T, B> p;

    public Not(Predicate<T, B> p) {
        this.p = requireNonNullArg(p, "p");
    }

    @Override
    public AstNode ast() {
        return new AstNode("not", Map.of(), List.of(p.ast()));
    }

    @Override
    public boolean test(T x) {
        return !p.test(x);
    }

    @Override
    public Predicate<T, B> bind(B parameterData) {
        return new Not<>(p.bind(parameterData));
    }
}
