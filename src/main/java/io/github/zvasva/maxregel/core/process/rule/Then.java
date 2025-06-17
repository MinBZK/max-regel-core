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
        RuleResult resultA = a.apply(factset, tracer);
        RuleResult resultB = b.apply(resultA.total(), tracer);
        FactSet update = resultA.update().union(resultB.update());
        return new RuleResult(update, resultB.total());
    }

    @Override
    public FactSet apply(FactSet factset) {
        if("identity".equals(a.op())){
            return b.apply(factset);
        }
        if("identity".equals(b.op())){
            return a.apply(factset);
        }
        return b.apply(a.apply(factset));
    }
}
