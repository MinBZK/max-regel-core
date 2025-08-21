package io.github.zvasva.maxregel.core.process.predicate;

import io.github.zvasva.maxregel.core.factset.FactSet;
import io.github.zvasva.maxregel.core.factset.FactSets;
import io.github.zvasva.maxregel.core.process.AstNode;
import io.github.zvasva.maxregel.core.term.Fact;

import java.util.List;
import java.util.Map;

/**
 * Converts a Fact {@link Predicate} into a FactSet {@link Predicate}.
 * The first predicate is used.
 * @author Arvid Halma
 */
public class AsFactsetPredicate extends AbstractPredicate<FactSet, FactSet> {
    private final Predicate<Fact, FactSet> factPredicate;

    public AsFactsetPredicate(Predicate<Fact, FactSet> factPredicate) {
        this.factPredicate = factPredicate;
    }

    @Override
    public boolean test(FactSet arg) {
        return !arg.isEmpty() && factPredicate.test(FactSets.first(arg));
    }

    @Override
    public AstNode ast() {
        return new AstNode("as_factset_predicate", Map.of(), List.of(factPredicate.ast()));
    }
}
