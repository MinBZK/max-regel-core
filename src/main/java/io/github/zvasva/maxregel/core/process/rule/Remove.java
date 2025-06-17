package io.github.zvasva.maxregel.core.process.rule;

import io.github.zvasva.maxregel.core.factset.FactSet;
import io.github.zvasva.maxregel.core.process.AstNode;

import static io.github.zvasva.maxregel.core.process.MaxRegelException.requireNonNullArg;

/**
 * Remove a part from a {@link FactSet}.
 */
public class Remove extends AbstractRule {

    private final String part;

    public Remove(String part) {
        this.part = requireNonNullArg(part, "part");
    }

    @Override
    public String op() {
        return "remove";
    }

    @Override
    public AstNode ast() {
        return createNode(part);
    }

    @Override
    public FactSet apply(FactSet factset) {
        return factset.remove(part);
    }
}
