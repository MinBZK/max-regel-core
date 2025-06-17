package io.github.zvasva.maxregel.core.process.predicate;

import io.github.zvasva.maxregel.core.process.AstNode;

import java.util.List;
import java.util.Map;

import static io.github.zvasva.maxregel.core.process.MaxRegelException.requireNonNullArg;

/**
 * A predicate that wraps another predicate and negates its result.
 *
 * @param <T> The type of objects that this predicate tests.
 * @author Arvid Halma
 */
public class Not<T, P> extends AbstractPredicate<T, P> {
    private final Predicate<T, P> p;

    public Not(Predicate<T, P> p) {
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
    public Predicate<T, P> bind(P parameterData) {
        return new Not<>(p.bind(parameterData));
    }
}
