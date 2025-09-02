package io.github.zvasva.maxregel.core.process.rule;


import io.github.zvasva.maxregel.core.factset.FactSet;
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


    /**
     * Add the update to the input to create the total factset/
     * @param facts input
     * @param update new facts
     * @return a factset with a new part (variable = some name) or parts (variable = *)
     */
    private FactSet assignToTotal(FactSet facts, FactSet update) {
        if ("*".equals(variable)) {
            FactSet result = EMPTY;

            for (String part : update.parts()) {
                result = result.union(update.get(part).setPart(part));
            }

            return facts.union(result);
        } else {
            return facts.union(update.setPart(variable));
        }
    }


    @Override
    public FactSet apply(FactSet facts) {
        // Pseudocode: factset.variable |= rule(factset)
        return assignToTotal(facts, rule.apply(facts));
    }

    @Override
    public RuleResult apply(FactSet facts, Tracer tracer) {
        FactSet update = rule.apply(facts);
        tracer.apply(this, update);
        FactSet total = assignToTotal(facts, update);
        return new RuleResult(update, total);
    }

}
