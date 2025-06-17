package io.github.zvasva.maxregel.core.process.rule;

import io.github.zvasva.maxregel.core.factset.FactSet;
import io.github.zvasva.maxregel.core.process.AstNode;

import static io.github.zvasva.maxregel.core.process.MaxRegelException.requireNonNullArg;

/**
 * Apply a rule at most once, its result will be stored and returned in later calls without evaluating the rule again.
 */
public class Cached extends AbstractRule {

    private final Rule rule;
    private FactSet cachedResult;

    public Cached(Rule rule) {
        this.rule = requireNonNullArg(rule, "rule");
    }

    @Override
    public String op() {
        return "cached";
    }

    @Override
    public AstNode ast() {
        return createNode(rule.ast());
    }

    @Override
    public FactSet apply(FactSet factset) {
        if(cachedResult == null) {
            cachedResult = rule.apply(factset);
        }
        return cachedResult;
    }
}
