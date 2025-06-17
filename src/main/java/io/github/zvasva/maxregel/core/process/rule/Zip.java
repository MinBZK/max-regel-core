package io.github.zvasva.maxregel.core.process.rule;

import io.github.zvasva.maxregel.core.factset.FactSet;
import io.github.zvasva.maxregel.core.factset.SinglePartFactSet;
import io.github.zvasva.maxregel.core.process.AstNode;
import io.github.zvasva.maxregel.core.process.BinaryOperation;
import io.github.zvasva.maxregel.core.term.Fact;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static io.github.zvasva.maxregel.core.process.MaxRegelException.requireNonNullArg;

/**
 * Merge two {@link FactSet}s pairwise, according to a given fact operation.
 * Makes a new {@link FactSet}, its elements are calculated from the function and the elements
 * of input lists occurring at the same position in both {@link FactSet}s.
 */
public class Zip extends AbstractRule {

    private final Rule selectA, selectB;
    private final BinaryOperation<Fact> operation;

    public Zip(BinaryOperation<Fact> operation, Rule selectA, Rule selectB) {
        this.operation = requireNonNullArg(operation, "operation");
        this.selectA = requireNonNullArg(selectA, "selectA");
        this.selectB = requireNonNullArg(selectB, "selectB");
    }

    public Rule getSelectA() {
        return selectA;
    }

    public Rule getSelectB() {
        return selectB;
    }

    @Override
    public String op() {
        return "zip";
    }

    @Override
    public AstNode ast() {
        return createNode( operation.ast(), selectA.ast(), selectB.ast());
    }

    @Override
    public FactSet apply(FactSet factset) {
        FactSet fsA = selectA.apply(factset);
        FactSet fsB = selectB.apply(factset);

        Iterator<Fact> iterA = fsA.iterator();
        Iterator<Fact> iterB = fsB.iterator();

        List<Fact> result = new ArrayList<>();
        while (iterA.hasNext() && iterB.hasNext()) {
            Fact fA = iterA.next();
            Fact fB = iterB.next();
            result.add(operation.apply(fA, fB));
        }
        return new SinglePartFactSet(result, "*");
    }
}
