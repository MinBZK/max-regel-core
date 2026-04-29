package io.github.zvasva.maxregel.core.process.predicate;

import io.github.zvasva.maxregel.core.factset.FactSet;
import io.github.zvasva.maxregel.core.process.AstNode;
import io.github.zvasva.maxregel.core.term.Fact;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import static io.github.zvasva.maxregel.core.process.MaxRegelException.requireNonNullArg;

/// A predicate that checks if all values of a given field are the same.
/// @author Arvid Halma
public class AllEqual extends AbstractPredicate<FactSet, FactSet> {

    private final String field;

    public AllEqual(String fieldName) {
        this.field = requireNonNullArg(fieldName, "field");
    }

    public String getField() {
        return field;
    }

    @Override
    public AstNode ast() {
        return new AstNode("all_equal", Map.of(), List.of(field));
    }

    @Override
    public boolean test(FactSet factset) {
        // Handle empty set case - consider all equal
        if (factset.isEmpty()) {
            return true;
        }

        // Get the first value to compare against
        Object prevValue = null;
        boolean prevValueSet = false;
        boolean allEqual = true;

        for (Fact fact : factset) {
            Object value = fact.get(field);

            if (!prevValueSet) {
                // Set the first value
                prevValue = value;
                prevValueSet = true;
            } else {
                // Compare with previous value
                if (!Objects.equals(prevValue, value)) {
                    allEqual = false;
                    break; // Early exit if we find a mismatch
                }
            }
        }

        return allEqual;
    }
}
