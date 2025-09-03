package io.github.zvasva.maxregel.core.process.rule;


import io.github.zvasva.maxregel.core.factset.FactSet;
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
                facts = facts.remove(part);
                updateResult = updateResult.union(update.get(part).setPart(part));
            }

            return new RuleResult(updateResult, facts.union(updateResult), true);
        } else {
            update = update.setPart(variable);
            return new RuleResult(update, facts.remove(variable).union(update), true);
        }
    }


    /**
     * Add a part to the input factset
     * @param facts input facts
     * @return input facts plus new part under variable name
     */
    @Override
    public FactSet apply(FactSet facts) {
        // Pseudocode: facts.variable = rule(facts)
        return assignToTotal(facts, rule.apply(facts)).total();
    }

    @Override
    public RuleResult apply(FactSet facts, Tracer tracer) {
        RuleResult ruleResult = rule.apply(facts, tracer); // body, RHS
        tracer.apply(this, ruleResult.update());
        return assignToTotal(facts, ruleResult.update());
    }

}
