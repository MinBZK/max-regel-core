package io.github.zvasva.maxregel.core.process.rule;

import io.github.zvasva.maxregel.core.factset.FactSet;
import io.github.zvasva.maxregel.core.process.AstNode;

import static io.github.zvasva.maxregel.core.process.MaxRegelException.requireNonNullArg;

/// Always evaluates to the given factset, ignoring its [#apply(FactSet)] argument.
/// This is a way to convert a [FactSet] into a [Rule].
public class Const extends AbstractRule {

    private final FactSet facts;

    public Const(FactSet facts) {
        this.facts = requireNonNullArg(facts, "facts");
    }

    @Override
    public String op() {
        return "const";
    }

    @Override
    public AstNode ast() {
        return createNode(facts);
    }

    @Override
    public FactSet apply(FactSet ignored) {
        return facts;
    }
}
