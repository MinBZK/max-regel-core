package io.github.zvasva.maxregel.core.process.rule;

import io.github.zvasva.maxregel.core.process.AstNode;
import io.github.zvasva.maxregel.core.process.BinaryOperation;
import io.github.zvasva.maxregel.core.term.Fact;
import io.github.zvasva.maxregel.core.term.MapTerm;
import io.github.zvasva.maxregel.core.term.Terms;

import java.util.function.BinaryOperator;

/**
 * Apply an arithmetic  of two fields of two parts.
 */
public class Arithmetic extends Zip {
    private final String op;
    private final String resultField;

    public Arithmetic(String resultField, String op, BinaryOperator<Double> operator, Rule selectA, Rule selectB) {
        super(
                BinaryOperation.fromJavaBinaryOperator(
                        (a, b) -> new Fact(MapTerm.of(resultField, operator.apply(getDouble(a), getDouble(b))))),
                selectA, selectB
        );
        this.op = op;
        this.resultField = resultField;
    }

    private static double getDouble(Fact fact){
        Object x = Terms.first(fact.getTerm(), Double.NaN);
        if(x instanceof Number n){
            return n.doubleValue();
        }
        return Double.NaN;
    }

    @Override
    public String op() {
        return op;
    }

    @Override
    public AstNode ast() {
        return createNode(resultField, getSelectA().ast(), getSelectB().ast());
    }

    /**
     * Divide two values.
     */
    public static class Add extends Arithmetic {
        public Add(String resultField, Rule selectA, Rule selectB) {
            super(resultField, "add", (a, b) -> a + b, selectA, selectB);
        }
    }

    /**
     * Subtract two values.
     */
    public static class Sub extends Arithmetic {
        public Sub(String resultField, Rule selectA, Rule selectB) {
            super(resultField, "sub", (a, b) -> a - b, selectA, selectB);
        }
    }

    /**
     * Multiply two values.
     */
    public static class Mul extends Arithmetic {
        public Mul(String resultField, Rule selectA, Rule selectB) {
            super(resultField, "mul", (a, b) -> a * b, selectA, selectB);
        }
    }

    /**
     * Divide two values.
     */
    public static class Div extends Arithmetic {
        public Div(String resultField, Rule selectA, Rule selectB) {
            super(resultField, "div", (a, b) -> a / b, selectA, selectB);
        }
    }

    /**
     * The maximum of two values.
     */
    public static class Max extends Arithmetic {
        public Max(String resultField, Rule selectA, Rule selectB) {
            super(resultField, "max", Math::max, selectA, selectB);
        }
    }

    /**
     * The minimum of two values.
     */
    public static class Min extends Arithmetic {
        public Min(String resultField, Rule selectA, Rule selectB) {
            super(resultField, "min", Math::min, selectA, selectB);
        }
    }

    /**
     * The first argument raised to the power of the second argument.
     */
    public static class Pow extends Arithmetic {
        public Pow(String resultField, Rule selectA, Rule selectB) {
            super(resultField, "pow", Math::pow, selectA, selectB);
        }
    }

}
