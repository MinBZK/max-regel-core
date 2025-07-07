package io.github.zvasva.maxregel.core.process.rule;

import io.github.zvasva.maxregel.core.process.AstNode;
import io.github.zvasva.maxregel.core.process.BinaryOperation;
import io.github.zvasva.maxregel.core.process.predicate.Comparator;
import io.github.zvasva.maxregel.core.term.Fact;
import io.github.zvasva.maxregel.core.term.MapTerm;

import java.util.function.BiFunction;

/**
 * Compare two facts and return the result as a term.
 */
public class Compare extends Zip {
    private final String op;
    private final String resultField;

    public Compare(String resultField, Comparator comparator, Rule selectA, Rule selectB) {
        this(resultField, comparator.op(), comparator::apply, selectA, selectB);
    }

    public Compare(String resultField, String op, BiFunction<Fact, Fact, Boolean> cmp, Rule selectA, Rule selectB) {
        super(
                BinaryOperation.fromJavaBinaryOperator(
                        (a, b) -> new Fact(MapTerm.of(resultField, cmp.apply(a, b)))),
                selectA, selectB
        );
        this.op = op;
        this.resultField = resultField;
    }

    @Override
    public String op() {
        return "compare";
    }

    @Override
    public AstNode ast() {
        return createNode(resultField, op, getSelectA().ast(), getSelectB().ast());
    }

}
