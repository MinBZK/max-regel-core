package io.github.zvasva.maxregel.core.process.rule;

import io.github.zvasva.maxregel.core.factset.FactSet;
import io.github.zvasva.maxregel.core.factset.FactSets;
import io.github.zvasva.maxregel.core.process.AstNode;
import io.github.zvasva.maxregel.core.term.Fact;
import io.github.zvasva.maxregel.core.term.MapTerm;

import java.util.Objects;

import static io.github.zvasva.maxregel.core.process.MaxRegelException.requireNonNullArg;

/// Checks if all values of a field in a FactSet are equal.
/// Returns a single fact with a boolean result.
public class AllEqual extends AbstractRule {

    private final Rule select;
    private final String field;
    private final io.github.zvasva.maxregel.core.process.predicate.AllEqual allEqualPredicate;

    public AllEqual(String field) {
        this(Rule.identity(), field);
    }

    public AllEqual(Rule select, String field) {
        this.select = requireNonNullArg(select, "select");
        this.field = requireNonNullArg(field, "field");
        this.allEqualPredicate = new io.github.zvasva.maxregel.core.process.predicate.AllEqual(field);
    }

    @Override
    public String op() {
        return "all_equal";
    }

    @Override
    public AstNode ast() {
        return createNode(select.ast(), field);
    }

    @Override
    public FactSet apply(FactSet factset) {
        FactSet selection = select.apply(factset);
        boolean output = allEqualPredicate.test(selection);
        return FactSets.create(MapTerm.of("all_equal", output));
    }

}