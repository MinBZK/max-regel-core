package io.github.zvasva.maxregel.core.process.rule;


import io.github.zvasva.maxregel.core.factset.FactSet;
import io.github.zvasva.maxregel.core.process.AstNode;
import io.github.zvasva.maxregel.core.process.MaxRegelException;
import io.github.zvasva.maxregel.core.process.predicate.Predicate;
import io.github.zvasva.maxregel.util.PrettyPrint;

import static io.github.zvasva.maxregel.core.factset.Empty.EMPTY;
import static io.github.zvasva.maxregel.core.process.MaxRegelException.requireNonNullArg;

/**
 * Make sure some condition holds, or throw an exception
 */
public class Assert extends AbstractRule {

    private final Rule select;
    private final Predicate<FactSet, FactSet> predicate;

    public Assert(Predicate<FactSet, FactSet> predicate) {
        this(Rule.identity(),predicate);
    }

    public Assert(Rule select, Predicate<FactSet, FactSet> predicate) {
        this.select = requireNonNullArg(select, "select");
        this.predicate = requireNonNullArg(predicate, "predicate");
    }

    @Override
    public String op() {
        return "assert";
    }

    @Override
    public AstNode ast() {
        return createNode(select.ast(), predicate);
    }

    @Override
    public FactSet apply(FactSet factset) {

        if (!predicate.test(select.apply(factset))) {
            throw new MaxRegelException("Assert failed: " + PrettyPrint.prettyPredicate(predicate));
        }
        return EMPTY;
    }
}
