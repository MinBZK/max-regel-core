package io.github.zvasva.maxregel.core.process.rule;

import io.github.zvasva.maxregel.core.factset.FactSet;
import io.github.zvasva.maxregel.core.process.AssignmentStructure;
import io.github.zvasva.maxregel.core.process.AstNode;
import io.github.zvasva.maxregel.core.process.Tracer;

import static io.github.zvasva.maxregel.core.process.MaxRegelException.requireNonNullArg;

/**
 * Select a part of the FactSet.
 */
public class From extends AbstractRule {

    private final String part;

    public From(String part) {
        this.part = requireNonNullArg(part, "part");
    }

    @Override
    public String op() {
        return "from";
    }

    @Override
    public AstNode ast() {
        return createNode(part);
    }

    @Override
    public FactSet apply(FactSet factset) {
        return factset.get(part);
    }

    @Override
    public RuleResult apply(FactSet facts, Tracer tracer, AssignmentStructure assignmentStructure) {
        if(assignmentStructure != null) {
            if(facts.has(part)){
                // we already have the part in the facts: great
                return super.apply(facts, tracer);
            } else {
                // we need to call the assignment first, to be sure we can call "from" later
                assignmentStructure.incrementEpoch();
                Assign assign = assignmentStructure.get(part);
                if (assign != null) {
                    RuleResult subResult = assign.apply(facts, tracer, assignmentStructure);
                    // now there should be the requested part
                    FactSet fromResult = apply(subResult.total());
                    return new RuleResult(fromResult, subResult.total());
                } else {
                    // non-existing part
                    return super.apply(facts, tracer);
                }
            }
        }
        // normal operation
        return super.apply(facts, tracer);
    }
}
