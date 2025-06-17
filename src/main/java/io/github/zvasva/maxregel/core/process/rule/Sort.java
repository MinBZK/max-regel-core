package io.github.zvasva.maxregel.core.process.rule;

import io.github.zvasva.maxregel.core.factset.FactSet;
import io.github.zvasva.maxregel.core.factset.SinglePartFactSet;
import io.github.zvasva.maxregel.core.process.AstNode;
import io.github.zvasva.maxregel.core.term.Fact;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static io.github.zvasva.maxregel.core.process.MaxRegelException.requireNonNullArg;

/**
 * Sorts a FactSet based on a specified field in either ascending or descending order.
 */
public class Sort extends AbstractRule {
    private final Rule select;
    private final String fieldName;
    private final boolean descending;

    /**
     * Creates a Sort rule that sorts the input FactSet by the specified field in ascending order.
     * @param fieldName The name of the field to sort by
     */
    public Sort(String fieldName) {
        this(Rule.identity(), fieldName, false);
    }

    /**
     * Creates a Sort rule that sorts the input FactSet by the specified field.
     * @param fieldName The name of the field to sort by
     * @param descending Whether to sort in descending order (true) or ascending order (false)
     */
    public Sort(String fieldName, boolean descending) {
        this(Rule.identity(), fieldName, descending);
    }

    /**
     * Creates a Sort rule that sorts the result of the select rule by the specified field.
     * @param select The rule to apply before sorting
     * @param fieldName The name of the field to sort by
     * @param descending Whether to sort in descending order (true) or ascending order (false)
     */
    public Sort(Rule select, String fieldName, boolean descending) {
        this.select = requireNonNullArg(select, "select");
        this.fieldName = requireNonNullArg(fieldName, "fieldName");
        this.descending = descending;
    }

    @Override
    public String op() {
        return "sort";
    }

    @Override
    public AstNode ast() {
        return createNode(select.ast(), fieldName, descending);
    }

    @Override
    public FactSet apply(FactSet factset) {
        FactSet selected = select.apply(factset);
        
        Comparator<Fact> comparator = Comparator.comparing(
            fact -> {
                Object value = fact.get(fieldName);
                if (value == null) return null;
                if (!(value instanceof Comparable)) {
                    throw new IllegalArgumentException("Field '" + fieldName + "' is not Comparable: " + value.getClass());
                }
                return (Comparable) value;
            },
            Comparator.nullsLast(Comparator.naturalOrder())
        );
        
        if (descending) {
            comparator = comparator.reversed();
        }
        
        List<Fact> sortedFacts = selected.stream()
            .sorted(comparator)
            .collect(Collectors.toList());
            
        return new SinglePartFactSet(sortedFacts);
    }
}
