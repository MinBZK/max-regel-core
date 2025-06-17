package io.github.zvasva.maxregel.core.process.rule;


import io.github.zvasva.maxregel.core.process.AstNode;

import java.util.LinkedHashMap;
import java.util.Map;

import static io.github.zvasva.maxregel.core.process.MaxRegelException.requireNonNullArg;

/**
 * Set/replace "variable" Factset, defined by a {@link Rule}.
 * The Assignment class implements the Rule interface, representing an assignment operation
 * that removes a possible old result, and adding the result of applying a rule.
 */
public abstract class Assign extends AbstractRule {
    protected final String variable;
    protected final Rule rule;
    protected final Map<String, Object> info;

    public Assign(String variable, Rule rule) {
        this(variable, rule, new LinkedHashMap<>(0));
    }

    public Assign(String variable, Rule rule, Map<String, Object> info) {
        this.variable = requireNonNullArg(variable, "variable");
        this.rule = requireNonNullArg(rule, "rule");
        this.info =  new LinkedHashMap<>(requireNonNullArg(info, "info"));
    }

    public String variable() {
        return variable;
    }

    @Override
    public Map<String, Object> info() {
        return info;
    }

    @Override
    public AstNode ast() {
        return createNode(variable, rule.ast());
    }

}
