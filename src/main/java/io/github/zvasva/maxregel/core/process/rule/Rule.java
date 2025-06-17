package io.github.zvasva.maxregel.core.process.rule;

import io.github.zvasva.maxregel.core.factset.FactSet;
import io.github.zvasva.maxregel.core.process.AssignmentStructure;
import io.github.zvasva.maxregel.core.process.Tracer;
import io.github.zvasva.maxregel.core.process.UnaryOperation;
import io.github.zvasva.maxregel.util.Collections;

import java.util.Map;

/**
 * Rule represents a transformation that can be applied to a {@link FactSet}.
 * Each Rule has a unique identifier and associated metadata.
 */
public interface Rule extends UnaryOperation<FactSet> {

    /**
     * An apply() implementation that allows for a tracer and assignment structure.
     * todo
     *
     * @param facts the input facts
     * @param tracer the tracer, that is called on the update (the new resulting facts after apply)
     * @return a tuple of the total and update facts.
     */
    default RuleResult apply(FactSet facts, Tracer tracer, AssignmentStructure assignmentStructure) {
        FactSet updates = apply(facts);
        tracer.apply(this, updates);
        return new RuleResult(updates, facts);
    }

    /**
     * An apply() implementation that allows for a tracer to be called.
     * <p>
     * The default implementation uses the simpler apply(facts) method, applies the tracer to the result,
     * and returns those results, along with the untouched input facts.
     *
     * @param facts the input facts
     * @param tracer the tracer, that is called on the update (the new resulting facts after apply)
     * @return a tuple of the total and update facts.
     */
    default RuleResult apply(FactSet facts, Tracer tracer) {
        FactSet updates = apply(facts);
        tracer.apply(this, updates);
        return new RuleResult(updates, facts);
    }

    /**
     * Metadata of this rule, mentioning "MaxRegel.core.rule" as source, i.e. some core rule as
     * opposed to a business rule from a business source down the line.
     * @return the meta data
     */
    @Override
    default Map<String, Object> info(){
        return Collections.map("source", "MaxRegel.core.rule");
    }

    /**
     * An identity rule (input factset = output factset)
     * @return a new Rule
     */
    static Rule identity() {
        return new io.github.zvasva.maxregel.core.process.rule.Identity();
    }

    /**
     * Chain two rules together: first this rule, then the other rule.
     * @param other the later rule
     * @return a new (combined) rule
     */
    default Rule then(Rule other) {
        return new io.github.zvasva.maxregel.core.process.rule.Then(this, other);
    }

}
