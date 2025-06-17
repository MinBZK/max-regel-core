package io.github.zvasva.maxregel.core.process.rule;

import io.github.zvasva.maxregel.core.factset.FactSet;
import io.github.zvasva.maxregel.core.process.AstNode;

import static io.github.zvasva.maxregel.core.factset.Empty.EMPTY;
import static io.github.zvasva.maxregel.core.process.MaxRegelException.requireNonNullArg;

/**
 * Apply a rule at most once, subsequent calls will return empty fact sets.
 */
public class Once extends AbstractRule {

    private boolean done;

    private final Rule rule;

    public Once(Rule rule) {
        this.rule = requireNonNullArg(rule, "rule");
        this.done = false;
    }

    @Override
    public String op() {
        return "once";
    }

    @Override
    public AstNode ast() {
        return createNode(rule.ast());
    }

    @Override
    public FactSet apply(FactSet factset) {
        if(!done) {
            done = true;
            return rule.apply(factset);
        }
        return EMPTY;
    }
}
