package io.github.zvasva.maxregel.core.process.rule;

import io.github.zvasva.maxregel.core.factset.FactSet;
import io.github.zvasva.maxregel.core.factset.FactSets;
import io.github.zvasva.maxregel.core.factset.SinglePartFactSet;
import io.github.zvasva.maxregel.core.process.AstNode;
import io.github.zvasva.maxregel.core.term.Fact;
import io.github.zvasva.maxregel.core.term.MapTerm;
import io.github.zvasva.maxregel.core.term.Terms;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static io.github.zvasva.maxregel.core.process.MaxRegelException.requireNonNullArg;

/**
 * An aggregate rule is a function where multiple values are processed together to form a single summary statistic.
 */
public class AggregateBy extends AbstractRule {

    private final Rule select;
    private final List<String> groupFields;
    private final String valueField;
    private final Aggregate aggregate;


    public AggregateBy(String groupField, String valueField, Aggregate aggregate) {
        this(Rule.identity(), List.of(groupField), valueField, aggregate);
    }

    public AggregateBy(List<String> groupFields, String valueField, Aggregate aggregate) {
        this(Rule.identity(), groupFields, valueField, aggregate);
    }

    public AggregateBy(Rule select, List<String> groupFields, String valueField, Aggregate aggregate) {
        this.select = requireNonNullArg(select, "select");;
        this.groupFields = requireNonNullArg(groupFields, "groupFields");
        this.valueField = requireNonNullArg(valueField, "valueField");
        this.aggregate = requireNonNullArg(aggregate, "aggregate");
    }


    @Override
    public String op() {
        return "aggregate_by";
    }

    @Override
    public AstNode ast() {
        return createNode(select.ast(), groupFields, valueField, aggregate.ast());
    }

    @Override
    public FactSet apply(FactSet factset) {
        Map<Fact, List<Fact>> groups = new LinkedHashMap<>();
        factset.stream().forEach(fact -> {
            final Fact key = new Fact(Terms.pick(fact.getTerm(), groupFields));
            if(!groups.containsKey(key)){
                groups.put(key, new ArrayList<>());
            }
            groups.get(key).add(fact);
        });
        String valueFieldName = aggregate.op() + "_" + valueField; // e.g. aggregate_max_age
        return new SinglePartFactSet(groups.entrySet().stream().map(entry -> {
            Object x = FactSets.value(aggregate.apply(new SinglePartFactSet(entry.getValue())));
            return new Fact(Terms.union(entry.getKey().getTerm(), MapTerm.of(valueFieldName, x)));
        }).toList());
    }

}
