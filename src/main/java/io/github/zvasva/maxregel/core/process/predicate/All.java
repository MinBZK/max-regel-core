package io.github.zvasva.maxregel.core.process.predicate;

import io.github.zvasva.maxregel.core.factset.FactSet;
import io.github.zvasva.maxregel.core.process.AstNode;
import io.github.zvasva.maxregel.core.term.Fact;

import java.util.List;
import java.util.Map;

import static io.github.zvasva.maxregel.core.process.MaxRegelException.requireNonNullArg;

/**
 * A predicate that checks if all of its facts pass a given test.
 * @author Arvid Halma
 */
public class All extends AbstractPredicate<FactSet, FactSet> {

    private final Predicate<Fact, FactSet> factPredicate;

    public All(Predicate<Fact, FactSet> factPredicate) {
        this.factPredicate = requireNonNullArg(factPredicate, "factPredicate");
    }

    @Override
    public AstNode ast() {
        return new AstNode("all", Map.of(), List.of(factPredicate.ast()));
    }

    @Override
    public boolean test(FactSet factset) {
        return factset.stream().allMatch(f -> factPredicate.test(f));
    }
}
