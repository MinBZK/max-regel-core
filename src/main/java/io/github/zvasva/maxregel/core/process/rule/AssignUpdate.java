package io.github.zvasva.maxregel.core.process.rule;


import io.github.zvasva.maxregel.core.process.AstNode;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Add "variable" Factset, defined by a {@link Rule}.
 * The Assignment class implements the Rule interface, representing an assignment operation
 * that joins the result of applying a rule.
 */
public class AssignUpdate extends Assign {

    public AssignUpdate(String variable, Rule rule) {
        this(variable, rule, new LinkedHashMap<>(0));
    }

    public AssignUpdate(String variable, Rule rule, Map<String, Object> info) {
        super(variable, rule, info, false);
    }

    @Override
    public String op() {
        return "assign_update";
    }

    @Override
    public AstNode ast() {
        return createNode(variable, body.ast());
    }


}
