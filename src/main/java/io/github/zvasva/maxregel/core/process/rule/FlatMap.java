package io.github.zvasva.maxregel.core.process.rule;

import io.github.zvasva.maxregel.core.factset.FactSet;
import io.github.zvasva.maxregel.core.factset.FactSets;
import io.github.zvasva.maxregel.core.factset.SinglePartFactSet;
import io.github.zvasva.maxregel.core.process.AstNode;
import io.github.zvasva.maxregel.core.term.Fact;

import java.util.ArrayList;
import java.util.List;

import static io.github.zvasva.maxregel.core.process.MaxRegelException.requireNonNullArg;

/**
 * Apply a Rule to all rows, each resulting in new rows (zero or more),
 * and collect them into a new {@link FactSet}.
 *
 * Consider using a {@link FactSet#factOperation()} when mapping fact to fact
 * in other cases.
 */
public class FlatMap extends AbstractRule {
    private final Rule select;
    private final Rule transform;

    public FlatMap(Rule transform) {
        this(Rule.identity(), transform);
    }

    public FlatMap(Rule select, Rule transform) {
        this.select = requireNonNullArg(select, "select");
        this.transform = requireNonNullArg(transform, "transform");
    }

    @Override
    public String op() {
        return "flatmap";
    }

    @Override
    public AstNode ast() {
        return createNode(select.ast(), transform.ast());
    }

    @Override
    public FactSet apply(FactSet factset) {
        List<Fact> transformedFacts = new ArrayList<>();
        for (Fact fact : select.apply(factset)) {
            transform.apply(FactSets.create(fact)).stream().forEach(transformedFacts::add);
        }
        return new SinglePartFactSet(transformedFacts);
    }
}
