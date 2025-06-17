package io.github.zvasva.maxregel.core.process.rule;

import io.github.zvasva.maxregel.core.factset.FactSet;
import io.github.zvasva.maxregel.core.process.AstNode;
import io.github.zvasva.maxregel.core.process.predicate.Predicate;
import io.github.zvasva.maxregel.core.term.Fact;

import static io.github.zvasva.maxregel.core.process.MaxRegelException.requireNonNullArg;

/**
 * Filter a given FactSet based on a provided Predicate.
 */
public class Filter extends AbstractRule {
    private final Rule select;
    private final Predicate<Fact, FactSet> predicate;

    public Filter(Predicate<Fact, FactSet> predicate) {
        this(Rule.identity(), predicate);
    }

    public Filter(Rule select, Predicate<Fact, FactSet> predicate) {
        this.select = requireNonNullArg(select, "select");
        this.predicate = requireNonNullArg(predicate, "predicate");
    }

    @Override
    public String op() {
        return "filter";
    }

    @Override
    public AstNode ast() {
        return createNode(select.ast(), predicate.ast());
    }

    @Override
    public FactSet apply(FactSet factset) {
        Predicate<Fact, FactSet> concretePred = predicate.bind(factset);
        return select.apply(factset).filter(concretePred);
    }
}
