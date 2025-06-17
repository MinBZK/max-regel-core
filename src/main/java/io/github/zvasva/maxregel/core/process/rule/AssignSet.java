package io.github.zvasva.maxregel.core.process.rule;


import io.github.zvasva.maxregel.core.factset.Concat;
import io.github.zvasva.maxregel.core.factset.FactSet;
import io.github.zvasva.maxregel.core.process.AssignmentStructure;
import io.github.zvasva.maxregel.core.process.Tracer;

import java.util.LinkedHashMap;
import java.util.Map;

import static io.github.zvasva.maxregel.core.factset.Empty.EMPTY;

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
        super(variable, rule, info);
    }

    @Override
    public String op() {
        return "assign_set";
    }

    @Override
    public FactSet apply(FactSet facts) {
        // facts.variable = rule(facts)
        if ("*".equals(variable)) {
            FactSet result = EMPTY;
            FactSet subResults = rule.apply(facts);

            for (String part : subResults.parts()) {
                facts = facts.remove(part);
                result = result.union(subResults.get(part).setPart(part));
            }

            return facts.union(result);
        } else {
            return new Concat(facts.remove(variable), rule.apply(facts).setPart(variable));
        }
    }

    @Override
    public RuleResult apply(FactSet facts, Tracer tracer) {
        FactSet update = rule.apply(facts).setPart(variable);
        tracer.apply(this, update);
        Concat total = new Concat(facts.remove(variable), update);
        return new RuleResult(update, total);
    }

    @Override
    public RuleResult apply(FactSet facts, Tracer tracer, AssignmentStructure assignmentStructure) {
        RuleResult result = rule.apply(facts, tracer, assignmentStructure);
        FactSet update = result.update().setPart(variable);
        tracer.apply(this, update);
        Concat total = new Concat(result.total().remove(variable), update);
        return new RuleResult(update, total);
    }
}
