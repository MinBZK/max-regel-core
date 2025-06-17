package io.github.zvasva.maxregel.core.process.factoperation;

import io.github.zvasva.maxregel.core.factset.FactSet;
import io.github.zvasva.maxregel.core.process.AstNode;
import io.github.zvasva.maxregel.core.process.UnaryOperation;
import io.github.zvasva.maxregel.core.process.rule.AbstractRule;
import io.github.zvasva.maxregel.core.term.Fact;
import io.github.zvasva.maxregel.core.term.MapTerm;
import io.github.zvasva.maxregel.core.term.Terms;
import io.github.zvasva.maxregel.core.term.Term;

import java.time.LocalDate;
import java.util.Map;

/**
 * Given a date field within a {@link Term}, add an age field.
 */
public class ComputeAge extends AbstractRule {
    private final String dateField;
    private final String newAgeField;

    public ComputeAge(String dateField, String newAgeField) {
        this.dateField = dateField;
        this.newAgeField = newAgeField;
    }
    
    public ComputeAge(String dateField) {
        this.dateField = dateField;
        this.newAgeField = "leeftijd";
    }

    @Override
    public String op() {
        return "compute_age";
    }

    public UnaryOperation<Fact> factOperation() {
        return UnaryOperation.of( arg -> {
            Map<String, Object> map = Terms.asMap(arg.getTerm());

            Object val = map.get(dateField);
            Integer age = null;
            if(val instanceof LocalDate d) {
                age = d.until(LocalDate.now()).getYears();
            } else {
                try {
                    LocalDate d = LocalDate.parse(val.toString());
                    age = d.until(LocalDate.now()).getYears();
                } catch (Exception ignored) {}
            }
            map.put(newAgeField, age);

            return new Fact(new MapTerm(map), arg.getInfo());
        });
    }

    @Override
    public FactSet apply(FactSet arg) {
        return arg.addFactOperation(factOperation());
    }

    @Override
    public AstNode ast() {
        return createNode(dateField, newAgeField);
    }
}
