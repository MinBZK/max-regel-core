package io.github.zvasva.maxregel.core.process.predicate;

import io.github.zvasva.maxregel.core.factset.FactSet;
import io.github.zvasva.maxregel.core.factset.FactSets;
import io.github.zvasva.maxregel.core.process.AstNode;
import io.github.zvasva.maxregel.core.term.Fact;

import java.util.List;
import java.util.Map;

import static io.github.zvasva.maxregel.core.process.MaxRegelException.requireNonNullArg;

/**
 * A predicate that checks if any of its facts pass a given test.
 * @author Arvid Halma
 */
public class Any extends AbstractPredicate<FactSet, FactSet> {

    private Predicate<Fact, FactSet> factPredicate;

    public Any(Predicate<Fact, FactSet> factPredicate) {
        this.factPredicate = requireNonNullArg(factPredicate, "factPredicate");
    }

    @Override
    public AstNode ast() {
        return new AstNode("any", Map.of(), List.of(factPredicate.ast()));
    }

    @Override
    public boolean test(FactSet factset) {
        return factset.stream().anyMatch(f -> factPredicate.test(f));
    }
}
