package io.github.zvasva.maxregel.core.process.predicate;

import io.github.zvasva.maxregel.core.factset.FactSet;
import io.github.zvasva.maxregel.core.factset.FactSets;
import io.github.zvasva.maxregel.core.process.AstNode;
import io.github.zvasva.maxregel.core.process.MaxRegelException;
import io.github.zvasva.maxregel.core.process.rule.Rule;
import io.github.zvasva.maxregel.core.term.Fact;
import io.github.zvasva.maxregel.core.term.Terms;
import io.github.zvasva.maxregel.util.NumberComparator;

import java.util.List;
import java.util.Map;

import static io.github.zvasva.maxregel.core.process.MaxRegelException.requireNonNullArg;

/**
 * A comparison function, which imposes a total ordering on some collection of objects.
 * It takes special care of comparing Numbers (e.g. Long &gt; Double).
 * @author Arvid Halma
 */
public class Comparator extends AbstractPredicate<Fact, FactSet> {
    private final String op;
    private final String field;
    private final Object y;

    private final int signForTrue;
    private final boolean includeEquals;

    protected Comparator(String op, int signForTrue, boolean includeEquals) {
        this.op = requireNonNullArg(op, "op");
        this.field = null;
        this.y = null;
        this.signForTrue = signForTrue;
        this.includeEquals = includeEquals;
    }

    public Comparator(String op, String field, Object y, int signForTrue, boolean includeEquals) {
        this.op = requireNonNullArg(op, "op");
        this.field = requireNonNullArg(field, "field");
        this.y = requireNonNullArg(y, "y");
        this.signForTrue = signForTrue;
        this.includeEquals = includeEquals;
    }

    public String getField() {
        return field;
    }

    public Object getY() {
        return y;
    }

    public String op() {
        return op;
    }

    @Override
    public AstNode ast() {
        return new AstNode(op,  Map.of(), List.of(field, y));
    }

    @Override
    public Predicate<Fact, FactSet> bind(FactSet parameterData) {
        if(y instanceof Rule r){
            Object yConcrete = FactSets.value(r.apply(parameterData));
            return new Comparator(op, field, yConcrete, signForTrue, includeEquals);
        }
        return this;
    }

    @Override
    public boolean test(Fact fact) {
        Object x = fact.get(field);
        return apply(x, y);
    }

    public boolean apply(Object x, Object y) {
        if(x instanceof Fact f)
            x = Terms.first(f.getTerm());

        if(y instanceof Fact f)
            y = Terms.first(f.getTerm());

        if(x instanceof FactSet fs)
            x = FactSets.value(fs);

        if(y instanceof FactSet fs)
            y = FactSets.value(fs);

        if(y instanceof Rule){
            throw new MaxRegelException("Comparison uses a rule to compare to, but did not bind() first: " + ast());
        }

        if (! (x instanceof Comparable comp)){
            return false;
        } else {
            if (x instanceof Number xn) {
                int sign = NumberComparator.cmp(xn, y);
                return signForTrue == sign || (includeEquals && sign == 0);
            }
            try {
                if(signForTrue == 0 && includeEquals) {
                    // just equals, faster for strings
                    return comp.equals(y);
                }
                int sign = comp.compareTo(y);
                return signForTrue == sign || (includeEquals && sign == 0);
            } catch (Exception e) {
                return false;
            }
        }
    }

    /**
     * Field equals. The FieldEq class is a Predicate implementation that evaluates whether a specified field of
     * a Fact object is equal to a given value.
     */
    public static class FieldEq extends Comparator {
        public FieldEq() {
            super("field_eq", 0, true);
        }

        public FieldEq(String field, Object y) {
            super("field_eq", field, y, 0, true);
        }
    }

    /**
     * Field greater than. The FieldGt class is a Predicate implementation that evaluates whether a specified field of
     * a Fact object is greater than a given value.
     */
    public static class FieldGt extends Comparator {
        public FieldGt() {
            super("field_gt", 1, false);
        }

        public FieldGt(String field, Object y) {
            super("field_gt", field, y, 1, false);
        }
    }

    /**
     * Field greater than or equals (>=). The FieldGt class is a Predicate implementation that evaluates whether a specified field of
     * a Fact object is greater than or equals a given value.
     */
    public static class FieldGeq extends Comparator {
        public FieldGeq() {
            super("field_geq", 1, true);
        }

        public FieldGeq(String field, Object y) {
            super("field_geq", field, y, 1, true);
        }
    }

    /**
     * Field less than. The FieldLt class is a Predicate implementation that evaluates whether a specified field of
     * a Fact object is less than a given value.
     */
    public static class FieldLt extends Comparator {
        public FieldLt() {
            super("field_lt", -1, false);
        }

        public FieldLt(String field, Object y) {
            super("field_lt", field, y, -1, false);
        }
    }

    /**
     * Field less than or equals (&leq;). The FieldLt class is a Predicate implementation that evaluates whether a specified field of
     * a Fact object is less than or equals a given value.
     */
    public static class FieldLeq extends Comparator {
        public FieldLeq() {
            super("field_leq", -1, true);
        }

        public FieldLeq(String field, Object y) {
            super("field_leq", field, y, -1, true);
        }
    }
}
