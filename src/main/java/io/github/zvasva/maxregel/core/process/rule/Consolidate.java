package io.github.zvasva.maxregel.core.process.rule;

import io.github.zvasva.maxregel.core.factset.FactSet;
import io.github.zvasva.maxregel.core.factset.MultiPartFactSet;
import io.github.zvasva.maxregel.core.process.AstNode;


/**
 * Load all data in memory, backed by a clean {@link MultiPartFactSet}
 */
public class Consolidate extends AbstractRule {

    public Consolidate() {}

    @Override
    public String op() {
        return "consolidate";
    }

    @Override
    public AstNode ast() {
        return createNode();
    }

    @Override
    public FactSet apply(FactSet factset) {
        return new MultiPartFactSet(factset);
    }
}
