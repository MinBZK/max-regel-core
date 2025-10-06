package io.github.zvasva.maxregel.core.process.predicate;

import io.github.zvasva.maxregel.core.factset.FactSet;
import io.github.zvasva.maxregel.core.process.AstNode;
import io.github.zvasva.maxregel.core.term.Fact;
import io.github.zvasva.maxregel.util.Coerce;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static io.github.zvasva.maxregel.core.process.MaxRegelException.requireNonNullArg;

/**
 * FieldEmpty checks whether a specified field of a Fact object is null, empty, blank or other kinds
 * of "falsy" values.
 *
 * @author Arvid Halma
 */
public class FieldEmpty extends AbstractPredicate<Fact, FactSet> {
    private final String field;

    public FieldEmpty(String field) {
        this.field = requireNonNullArg(field, "field");
    }

    @Override
    public AstNode ast() {
        return new AstNode("field_empty", Map.of(), List.of(field));
    }

    @Override
    public boolean test(Fact fact) {
        return Coerce.asBoolean(fact.get(field));
    }
}
