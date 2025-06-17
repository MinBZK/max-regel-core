package io.github.zvasva.maxregel.core.process.predicate;

import io.github.zvasva.maxregel.core.factset.FactSet;
import io.github.zvasva.maxregel.core.process.AstNode;
import io.github.zvasva.maxregel.core.term.Fact;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * {@link Predicate} utilities.
 *
 * @author Arvid Halma
 */
public class Predicates {

    /**
     * Check if all predicates hold
     * @param predicates all predicates to check
     * @return a single predicate that combines them
     * @param <T> the argument type
     */
    @SafeVarargs
    public static <T> Predicate<T, ?> all(Predicate<T, Object> ... predicates) {
        return Arrays.stream(predicates).reduce(new True<>(), Predicate::and);
    }

    /**
     * Check if some predicates hold
     * @param predicates all predicates to check
     * @return a single predicate that combines them
     * @param <T> the argument type
     */
    @SafeVarargs
    public static <T> Predicate<T, ?> any(Predicate<T, Object> ... predicates) {
        return Arrays.stream(predicates).reduce(new False<>(), Predicate::or);
    }

    public static Predicate<Fact, ?> parse(AstNode node) {
        if (node == null) {
            return null;
        }

        final List<?> args = node.args();
        return switch (node.op()) {
            case "field_eq",  "==" -> new Comparator.FieldEq(args.get(0).toString(), args.get(1));
            case "field_gt",  ">" -> new Comparator.FieldGt(args.get(0).toString(), args.get(1));
            case "field_geq", ">=" -> new Comparator.FieldGeq(args.get(0).toString(), args.get(1));
            case "field_lt",  "<" -> new Comparator.FieldLt(args.get(0).toString(), args.get(1));
            case "field_leq", "<=" -> new Comparator.FieldLeq(args.get(0).toString(), args.get(1));
            case "field_contains" -> new FieldContains(args.get(0).toString(), args.get(1).toString());
            case "field_in", "in" -> new FieldIn(args.get(0).toString(), args.get(1).toString()); // todo: list case
            case "not" -> new Not<>(parse((AstNode) args.get(0)));
            case "and", "&" -> new And<>((Predicate<Fact, FactSet>) parse((AstNode) args.get(0)), (Predicate<Fact, FactSet>) parse((AstNode) args.get(1)));
            case "or", "|" -> new Or<>((Predicate<Fact, FactSet>) parse((AstNode) args.get(0)), (Predicate<Fact, FactSet>) parse((AstNode) args.get(1)));

            default -> throw new IllegalArgumentException("Unsupported predicate function name: " + node.op());
        };
    }

    /**
     * Evaluates always to true, independent of the argument.
     * @param <T> argument type
     */
    public static class True<T> extends AbstractPredicate<T, Object> {
        @Override
        public boolean test(Object arg) {return true;}

        @Override
        public AstNode ast() {return new AstNode("true", Map.of(), List.of());}
    }

    /**
     * Evaluates always to false, independent of the argument.
     * @param <T> argument type
     */
    public static class False<T> extends AbstractPredicate<T, Object> {
        @Override
        public boolean test(Object arg) {return false;}

        @Override
        public AstNode ast() {return new AstNode("false", Map.of(), List.of());}
    }
}
