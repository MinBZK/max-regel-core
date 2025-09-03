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
    private RuleResult assignToTotal(FactSet facts, FactSet update) {
        if ("*".equals(variable)) {
            FactSet updateResult = EMPTY;

            for (String part : update.parts()) {
                updateResult = updateResult.union(update.get(part).setPart(part));
            }

            return new RuleResult(updateResult, facts.union(updateResult), true);
        } else {
            update = update.setPart(variable);
            return new RuleResult(update, facts.union(update), true);
        }
    }


    @Override
    public FactSet apply(FactSet facts) {
        // Pseudocode: factset.variable |= rule(factset)
        return assignToTotal(facts, rule.apply(facts)).total();
    }

    @Override
    public RuleResult apply(FactSet facts, Tracer tracer) {
        RuleResult ruleResult = rule.apply(facts, tracer); // body, RHS
        tracer.apply(this, ruleResult.update());
        return assignToTotal(facts, ruleResult.update());
    }

}
