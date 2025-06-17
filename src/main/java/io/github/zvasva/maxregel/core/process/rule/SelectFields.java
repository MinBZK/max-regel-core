package io.github.zvasva.maxregel.core.process.rule;

import io.github.zvasva.maxregel.core.factset.FactSet;
import io.github.zvasva.maxregel.core.process.AstNode;
import io.github.zvasva.maxregel.core.process.UnaryOperation;
import io.github.zvasva.maxregel.core.term.Fact;
import io.github.zvasva.maxregel.core.term.MapTerm;
import io.github.zvasva.maxregel.core.term.Term;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static io.github.zvasva.maxregel.core.process.MaxRegelException.requireNonNullArg;

/**
 * Just pick certain fields from each Fact's  {@link Term}s.
 */
public class SelectFields extends AbstractRule {
    private final Rule select;
    private final List<String> fieldNames;

    
    public SelectFields(Rule select, List<String> fieldNames) {
        this.select = requireNonNullArg(select, "select");
        this.fieldNames = requireNonNullArg(fieldNames, "fieldNames");
    }

    public SelectFields(List<String> fieldNames) {
        this(Rule.identity(), fieldNames);
    }

    public SelectFields(String ... fieldNames) {
        this(Arrays.asList(fieldNames));
    }

    @Override
    public String op() {
        return "select";
    }

    public UnaryOperation<Fact> factOperation() {
        return UnaryOperation.of(arg -> {
            Term term = arg.getTerm();
            Map<String, Object> map = new LinkedHashMap<>();
            for (String fieldName : fieldNames) {
                map.put(fieldName, term.get(fieldName));
            }

            return new Fact(new MapTerm(map), arg.getInfo());
        });
    }

    @Override
    public FactSet apply(FactSet facts) {
        return select.apply(facts).addFactOperation(factOperation());
    }

    @Override
    public AstNode ast() {
        return createNode(select.ast(), fieldNames);
    }
}
