package io.github.zvasva.maxregel.core.process.rule;

import io.github.zvasva.maxregel.core.factset.FactSet;
import io.github.zvasva.maxregel.core.process.AstNode;
import io.github.zvasva.maxregel.core.process.BinaryOperation;
import io.github.zvasva.maxregel.core.term.Fact;
import io.github.zvasva.maxregel.core.term.Terms;
import io.github.zvasva.maxregel.core.term.Term;

/**
 * Merge terms of two {@link FactSet}s pairwise, by taking the union of the corresponding {@link Term}s.
 */
public class Merge extends Zip {

    public Merge(Rule selectA, Rule selectB) {
        super(
                BinaryOperation.fromJavaBinaryOperator((a, b) -> new Fact(Terms.union(a.getTerm(), b.getTerm()))),
                selectA,
                selectB
        );
    }

    @Override
    public String op() {
        return "merge";
    }

    @Override
    public AstNode ast() {
        return createNode(getSelectA().ast(), getSelectB().ast());
    }

}
