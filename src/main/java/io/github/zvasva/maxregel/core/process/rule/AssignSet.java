package io.github.zvasva.maxregel.core.process.rule;


import io.github.zvasva.maxregel.core.process.AstNode;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Set/replace "variable" Factset, defined by a {@link Rule}.
 * The Assignment class implements the Rule interface, representing an assignment operation
 * that removes a possible old result, and adding the result of applying a rule.
 */
public class AssignSet extends Assign {

    public AssignSet(String variable, Rule rule) {
        this(variable, rule, new LinkedHashMap<>(0));
    }

    public AssignSet(String variable, Rule rule, Map<String, Object> info) {
        super(variable, rule, info, true);
    }

    @Override
    public String op() {
        return "assign_set";
    }

    @Override
    public AstNode ast() {
        return createNode(variable, body.ast());
    }

}
