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
 * FieldContains checks whether a specified field of a Fact object contains a specific value.
 * If the field is a list, the query element is checked on equality.
 * If the field is a string, the query should be a substring.
 *
 * @author Arvid Halma
 */
public class FieldContains extends AbstractPredicate<Fact, FactSet> {
    private final String field;
    private final String y;

    public FieldContains(String field, String y) {
        this.field = requireNonNullArg(field, "field");
        this.y = requireNonNullArg(y, "y");
    }

    @Override
    public AstNode ast() {
        return new AstNode("field_contains", Map.of(), List.of(field, y));
    }

    @Override
    public boolean test(Fact fact) {
        Object x = fact.get(field);
        if(x == null) {
            return false;
        } else if(x instanceof Collection<?> collection) {
            return collection.stream().anyMatch( y -> Objects.equals(y, this.y));
        } else {
            return x.toString().contains(y);
        }
    }
}
