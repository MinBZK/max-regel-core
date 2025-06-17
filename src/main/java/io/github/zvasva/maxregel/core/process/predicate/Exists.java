package io.github.zvasva.maxregel.core.process.predicate;

import io.github.zvasva.maxregel.core.factset.FactSet;
import io.github.zvasva.maxregel.core.process.AstNode;

import java.util.List;
import java.util.Map;

/**
 * A predicate that checks if the provided FactSet is not empty.
 * @author Arvid Halma
 */
public class Exists extends AbstractPredicate<FactSet, FactSet> {

    public Exists() {
    }

    @Override
    public AstNode ast() {
        return new AstNode("exists", Map.of(), List.of());
    }

    @Override
    public boolean test(FactSet factset) {
        return !factset.isEmpty();
    }
}
