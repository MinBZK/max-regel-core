package io.github.zvasva.maxregel.core.process.rule;

import io.github.zvasva.maxregel.core.factset.FactSet;
import io.github.zvasva.maxregel.core.factset.FactSets;
import io.github.zvasva.maxregel.core.process.AstNode;
import io.github.zvasva.maxregel.core.term.Fact;
import io.github.zvasva.maxregel.core.term.MapTerm;
import io.github.zvasva.maxregel.core.term.Terms;
import io.github.zvasva.maxregel.util.NumberComparator;

import java.util.function.BiFunction;

import static io.github.zvasva.maxregel.core.process.MaxRegelException.requireNonNullArg;

/**
 * An aggregate rule is a function where multiple values are processed together to form a single summary statistic.
 */
public class Aggregate extends AbstractRule {

    private final Rule select;
    private final String op;
    private final BiFunction<Object,Object,Object> reduce;
    private final Object defaultValue;

    private Aggregate(Rule select, String op) {
        this.select = requireNonNullArg(select, "select");
        this.op = requireNonNullArg(op, "op");
        reduce = null;
        defaultValue = null;
    }

    public Aggregate(Rule select, String op, BiFunction<Object,Object,Object> reduce, Object defaultValue) {
        this.select = requireNonNullArg(select, "select");
        this.op = requireNonNullArg(op, "op");
        this.reduce = requireNonNullArg(reduce, "reduce");
        this.defaultValue = defaultValue;
    }

    @Override
    public String op() {
        return op;
    }

    @Override
    public AstNode ast() {
        return createNode(select.ast());
    }

    @Override
    public FactSet apply(FactSet factset) {
        FactSet selection = select.apply(factset);
        Object y = defaultValue;
        for (Fact fact : selection) {
            y = reduce.apply(y, Terms.first(fact.getTerm()));
        }
        return FactSets.create(MapTerm.of(op, y));
    }

    /**
     * Count
     */
    public static class Count extends Aggregate {
        public Count() {
            this( Rule.identity());
        }

        public Count(Rule select) {
            super(select, "aggregate_count");
        }

        @Override
        public FactSet apply(FactSet factset) {
            return FactSets.create(MapTerm.of(op(), factset.size()));
        }
    }

    /**
     * Add all values of each term's first value.
     */
    public static class Sum extends Aggregate {
        public Sum() {
            this( Rule.identity());
        }

        public Sum(Rule select) {
            super(select,"aggregate_sum", (Object a, Object b) -> {
                if(a == null) {
                    return b;
                }
                if(b == null) {
                    return a;
                }
                if((a instanceof Number nA) && (b instanceof Number nB)) {
                    return nA.doubleValue() + nB.doubleValue();
                } else {
                    return Double.NaN;
                }
            }, null);
        }
    }

    /**
     * Determine the minium value of each term's first value.
     */
    public static class Min extends Aggregate {
        public Min() {
            this( Rule.identity());
        }

        public Min(Rule select) {
            super(select,"aggregate_min", (Object a, Object b) -> {
                switch (a) {
                    case null -> {
                        return b;
                    }
                    case Number n -> {
                        int sign = NumberComparator.cmp(n, b);
                        return sign <= 0 ? a : b;
                    }
                    case Comparable comp -> {
                        if (b == null) {
                            return a;
                        } else if (comp.compareTo(b) <= 0) {
                            return a;
                        } else {
                            return b;
                        }
                    }
                    default -> {
                        return a;
                    }
                }
            }, null);
        }
    }

    /**
     * Determine the maximum value of each term's first value.
     */
    public static class Max extends Aggregate {
        public Max() {
            this( Rule.identity());
        }

        public Max(Rule select) {
            super(select,"aggregate_max", (Object a, Object b) -> {
                switch (a) {
                    case null -> {
                        return b;
                    }
                    case Number n -> {
                        int sign = NumberComparator.cmp(n, b);
                        return sign > 0 ? a : b;
                    }
                    case Comparable comp -> {
                        if (b == null) {
                            return a;
                        } else if (comp.compareTo(b) > 0) {
                            return a;
                        } else {
                            return b;
                        }
                    }
                    default -> {
                        return a;
                    }
                }
            }, null);
        }
    }
}
