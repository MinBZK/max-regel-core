package io.github.zvasva.maxregel.core.process.rule;

import io.github.zvasva.maxregel.core.factset.FactSet;
import io.github.zvasva.maxregel.core.factset.FactSets;
import io.github.zvasva.maxregel.core.process.AstNode;
import io.github.zvasva.maxregel.core.term.MapTerm;

import static io.github.zvasva.maxregel.core.process.MaxRegelException.requireNonNullArg;

/**
 * Stores the size of a FactSet in a fact with a chosen variable/field name.
 */
public class Count extends AbstractRule {

    private final Rule select;
    private final String variable;

    public Count() {
        this(Rule.identity(),"count");
    }

    public Count(String variable) {
        this(Rule.identity(), variable);
    }

    public Count(Rule select, String variable) {
        this.select = requireNonNullArg(select, "select");
        this.variable = requireNonNullArg(variable, "variable");
    }

    @Override
    public String op() {
        return "count";
    }

    @Override
    public AstNode ast() {
        return createNode(select.ast(), variable);
    }

    @Override
    public FactSet apply(FactSet factset) {
        FactSet selection = select.apply(factset);
        return FactSets.create(MapTerm.of(variable, selection.size()));
    }
}
