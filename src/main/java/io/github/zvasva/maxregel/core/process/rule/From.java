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

}
