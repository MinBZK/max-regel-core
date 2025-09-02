package io.github.zvasva.maxregel.core.process.rule;


import io.github.zvasva.maxregel.core.factset.FactSet;
import io.github.zvasva.maxregel.core.process.AssignmentStructure;
import io.github.zvasva.maxregel.core.process.Tracer;

import java.util.LinkedHashMap;
import java.util.Map;

import static io.github.zvasva.maxregel.core.factset.Empty.EMPTY;

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
        super(variable, rule, info);
    }

    @Override
    public String op() {
        return "assign_update";
    }

    @Override
    public FactSet apply(FactSet facts) {
        // factset.variable |= rule(factset)
        if ("*".equals(variable)) {
            FactSet result = EMPTY;
            FactSet subResults = rule.apply(facts);

            for (String part : subResults.parts()) {
                result = result.union(subResults.get(part).setPart(part));
            }

            return facts.union(result);
        } else {
            return facts.union(rule.apply(facts).setPart(variable));
        }
    }

    @Override
    public RuleResult apply(FactSet facts, Tracer tracer) {
        FactSet update = rule.apply(facts).setPart(variable);
        tracer.apply(this, update);
        FactSet total = facts.union(update);
        return new RuleResult(update, total);
    }

}
