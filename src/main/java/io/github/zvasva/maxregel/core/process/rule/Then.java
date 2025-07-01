package io.github.zvasva.maxregel.core.process.rule;

import io.github.zvasva.maxregel.core.factset.FactSet;
import io.github.zvasva.maxregel.core.process.AstNode;
import io.github.zvasva.maxregel.core.process.Tracer;

import static io.github.zvasva.maxregel.core.process.MaxRegelException.requireNonNullArg;

/**
 * First apply rule a, then apply rule b on the result.
 */
public class Then extends AbstractRule {

    private final Rule a, b;

    public Then(Rule a, Rule b) {
        this.a = requireNonNullArg(a, "a");
        this.b = requireNonNullArg(b, "b");
    }

    @Override
    public String op() {
        return "then";
    }

    @Override
    public AstNode ast() {
        return createNode(a.ast(), b.ast());
    }

    @Override
    public RuleResult apply(FactSet factset, Tracer tracer) {
        // Skip identity rules to avoid unnecessary computation
        if("identity".equals(a.op())){
            return b.apply(factset, tracer);
        }
        if("identity".equals(b.op())){
            return a.apply(factset, tracer);
        }

        if(a instanceof ReturnIf ri){
            if(ri.condition(factset)) {
                // Just return the result of the ReturnIf, don't apply b
                return ri.result(factset, tracer);
            } else {
                // The condition was not met, so we can apply b
                return b.apply(factset, tracer);
            }
        }

        // The expected case: apply a, then b (with bookkeeping of updates and total results)
        RuleResult resultA = a.apply(factset, tracer);
        RuleResult resultB = b.apply(resultA.total(), tracer);
        FactSet update = resultA.update().union(resultB.update());
        return new RuleResult(update, resultB.total());
    }

    @Override
    public FactSet apply(FactSet factset) {
        // Skip identity rules to avoid unnecessary computation
        if("identity".equals(a.op())){
            return b.apply(factset);
        }
        if("identity".equals(b.op())){
            return a.apply(factset);
        }

        if(a instanceof ReturnIf ri){
            if(ri.condition(factset)) {
                // Just return the result of the ReturnIf, don't apply b
                return ri.result(factset);
            } else {
                // The condition was not met, so we can apply b
                return b.apply(factset);
            }
        }

        // The expected case: apply a, then b
        return b.apply(a.apply(factset));
    }
}
