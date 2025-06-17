package io.github.zvasva.maxregel.core.process.rule;

import io.github.zvasva.maxregel.core.factset.FactSet;
import io.github.zvasva.maxregel.core.factset.SinglePartFactSet;
import io.github.zvasva.maxregel.core.process.AstNode;
import io.github.zvasva.maxregel.util.Iters;

import static io.github.zvasva.maxregel.core.process.MaxRegelException.requireNonNullArg;

/**
 * Retrieve at most n facts from a FactSet.
 */
public class Limit extends AbstractRule {

    private final Rule select;
    private final long n;

    public Limit(long n) {
        this(Rule.identity(), n);
    }

    public Limit(Rule select, long n) {
        this.select = requireNonNullArg(select, "select");
        this.n = n;
    }

    @Override
    public String op() {
        return "limit";
    }

    @Override
    public AstNode ast() {
        return createNode(select.ast(), n);
    }

    @Override
    public FactSet apply(FactSet factset) {
        return new SinglePartFactSet(Iters.iterable(select.apply(factset).stream().limit(n).iterator()));
    }
}
