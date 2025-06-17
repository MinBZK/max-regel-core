package io.github.zvasva.maxregel.core.process.predicate;

import io.github.zvasva.maxregel.core.factset.FactSet;
import io.github.zvasva.maxregel.core.process.AstNode;
import io.github.zvasva.maxregel.core.term.Fact;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static io.github.zvasva.maxregel.core.process.MaxRegelException.requireNonNullArg;

/**
 * Field is contained in a given list. FieldIn is a Predicate implementation that checks if the value of a given field
 * in a Fact object is part of a specified list of values.
 *
 * @author Arvid Halma
 */
public class FieldIn extends AbstractPredicate<Fact, FactSet> {
    private final String field;
    private final Object ys;

    public FieldIn(String field, Collection<?> ys) {
        this.field = requireNonNullArg(field, "field");
        this.ys = requireNonNullArg(ys, "ys");
    }

    public FieldIn(String field, String ys) {
        this.field = field;
        this.ys = ys;
    }

    @Override
    public AstNode ast() {
        return new AstNode("field_in", Map.of(), List.of(field, ys));
    }

    @Override
    public boolean test(Fact fact) {
        Object x = fact.get(field);
        if(x == null) {
            return false;
        }
        if(ys instanceof Collection<?> collection) {
            return collection.stream().anyMatch( y -> Objects.equals(x, y));
        } else {
            return ys.toString().contains(x.toString());
        }

    }
}
