package io.github.zvasva.maxregel.core.process.rule;


import io.github.zvasva.maxregel.core.factset.FactSet;
import io.github.zvasva.maxregel.core.process.Tracer;

import java.util.LinkedHashMap;
import java.util.Map;

import static io.github.zvasva.maxregel.core.factset.Empty.EMPTY;
import static io.github.zvasva.maxregel.core.process.MaxRegelException.requireNonNullArg;

/**
 * Set/replace "variable" Factset, defined by a {@link Rule}.
 * The Assignment class implements the Rule interface, representing an assignment operation
 * that removes a possible old result, and adding the result of applying a rule.
 */
public abstract class Assign extends AbstractRule {
    protected final String variable;
    protected final Rule body;
    protected final Map<String, Object> info;
    protected final boolean overwrite;

    public Assign(String variable, Rule body) {
        this(variable, body, new LinkedHashMap<>(0), true);
    }

    public Assign(String variable, Rule body, Map<String, Object> info, boolean overwrite) {
        this.variable = requireNonNullArg(variable, "variable");
        this.body = requireNonNullArg(body, "body");
        this.info =  new LinkedHashMap<>(requireNonNullArg(info, "info"));
        this.overwrite = overwrite;
    }

    public String variable() {
        return variable;
    }

    @Override
    public Map<String, Object> info() {
        return info;
    }

    /**
     * Add the newlyAssigned to the input to create the output factset
     * @param facts input
     * @param update new facts
     * @return a factset with a new part (variable = some name) or parts (variable = *)
     */
    private RuleResult assignToTotal(FactSet facts, FactSet update) {
        if ("*".equals(variable)) {
            FactSet newlyAssigned = EMPTY;

            for (String part : update.parts()) {
                if(overwrite) {
                    facts = facts.remove(part);
                }
                newlyAssigned = newlyAssigned.union(update.get(part).setPart(part));
            }

            return new RuleResult(facts.union(newlyAssigned), newlyAssigned);
        } else {
            update = update.setPart(variable);
            if(overwrite) {
                facts = facts.remove(variable);
            }
            return new RuleResult(facts.union(update), update);
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
        return assignToTotal(facts, body.apply(facts)).output();
    }

    @Override
    public RuleResult apply(FactSet facts, Tracer tracer) {
        RuleResult ruleResult = body.apply(facts, tracer); // body, RHS
        tracer.apply(this, ruleResult.output());
        return assignToTotal(facts, ruleResult.output());
    }

}
