package io.github.zvasva.maxregel.core.process.rule;

import io.github.zvasva.maxregel.core.factset.FactSet;
import io.github.zvasva.maxregel.core.process.AstNode;

import static io.github.zvasva.maxregel.core.process.MaxRegelException.requireNonNullArg;

/**
 * Create a combined factset from two factset selections.
 */
public class Concat extends AbstractRule {
    private final Rule selectA, selectB;

    public Concat(Rule selectA, Rule selectB) {
        this.selectA = requireNonNullArg(selectA, "selectA");
        this.selectB = requireNonNullArg(selectB, "selectB");
    }

    @Override
    public String op() {
        return "concat";
    }

    @Override
    public AstNode ast() {
        return createNode( selectA.ast(), selectB.ast());
    }

    @Override
    public FactSet apply(FactSet factset) {
        FactSet fsA = selectA.apply(factset);

        if(fsA.isEmpty()) {
            return selectB.apply(factset);
        }

        FactSet fsB = selectB.apply(factset);
        if(fsB.isEmpty()) {
            return fsA;
        }
        return new io.github.zvasva.maxregel.core.factset.Concat(fsA, fsB);
    }
}
