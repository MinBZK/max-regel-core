package io.github.zvasva.maxregel.core.process.rule;

import io.github.zvasva.maxregel.core.factset.FactSet;
import io.github.zvasva.maxregel.core.process.AstNode;

/**
 * A rule that does nothing.
 */
public class Identity extends AbstractRule {


    public Identity() {}

    @Override
    public String op() {
        return "identity";
    }

    @Override
    public AstNode ast() {
        return createNode();
    }

    @Override
    public FactSet apply(FactSet factset) {
        return factset;
    }
}
