package io.github.zvasva.maxregel.core.process.rule;

import io.github.zvasva.maxregel.core.factset.FactSet;
import io.github.zvasva.maxregel.core.factset.FactSets;
import io.github.zvasva.maxregel.core.process.AstNode;
import io.github.zvasva.maxregel.core.term.Fact;
import io.github.zvasva.maxregel.core.term.MapTerm;

import java.util.Objects;

import static io.github.zvasva.maxregel.core.process.MaxRegelException.requireNonNullArg;

/**
 * Checks if all values of a field in a FactSet are equal.
 * Returns a single fact with a boolean result.
 */
public class AllEqual extends AbstractRule {

    private final Rule select;
    private final String fieldName;

    public AllEqual(String fieldName) {
        this(Rule.identity(), fieldName);
    }

    public AllEqual(Rule select, String fieldName) {
        this.select = requireNonNullArg(select, "select");
        this.fieldName = requireNonNullArg(fieldName, "fieldName");
    }

    @Override
    public String op() {
        return "allEqual";
    }

    @Override
    public AstNode ast() {
        return createNode(select.ast(), fieldName);
    }

    @Override
    public FactSet apply(FactSet factset) {
        FactSet selection = select.apply(factset);
        
        // Handle empty set case - consider all equal
        if (selection.isEmpty()) {
            return FactSets.create(MapTerm.of("allEqual", true));
        }

        // Get the first value to compare against
        Object prevValue = null;
        boolean prevValueSet = false;
        boolean allEqual = true;

        for (Fact fact : selection) {
            Object value = fact.get(fieldName);
            
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

        return FactSets.create(MapTerm.of("allEqual", allEqual));
    }
}