package io.github.zvasva.maxregel.core.process;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.UnaryOperator;

/**
 * Represents a function on a single operand that produces a result of the same type as its operand.
 * <p>
 * Why not use {@link UnaryOperator} instead? That is a pure function
 * and can't do much else than being applied. This version allows for mapping to an
 * abstract syntax tree (AST). And it carries arbitrary meta-data in {@link #info()}.
 *
 * @author Arvid Halma
 * @param <T> the argument and result type
 */
public interface UnaryOperation<T> {

    /**
     * An identifier/operator name.
     * @return name
     */
    String op();

    /**
     * Arbitrary meta data.
     * @return the associated info
     */
    Map<String, Object> info();


    /**
     * Apply this function and return a new value
     * @param arg the input
     * @return the output
     */
    T apply(T arg);


    /**
     * Predicate as abstract syntax tree.
     * @return s-expression
     */
    AstNode ast();



    /**
     * Wrap a {@link UnaryOperator} (plain Java version) as an {@link UnaryOperation}
     * @param operator the function: T -> T
     * @param <T> the argument and result type
     * @return new operation
     */
    static <T> UnaryOperation<T> of(UnaryOperator<T> operator) {
        return new UnaryOperation<>() {
            @Override
            public String op() {
                return "?";
            }

            @Override
            public Map<String, Object> info() {
                return new LinkedHashMap<>(0);
            }

            @Override
            public T apply(T arg) {
                return operator.apply(arg);
            }

            @Override
            public AstNode ast() {
                return createNode();
            }
        };
    }

    /**
     * Get the pure Java {@link UnaryOperator} (just apply, no info() or ast())
     * @return the simpler java.util.function version of this operation.
     */
    default UnaryOperator<T> asJavaUnaryOperator() {
        return UnaryOperation.this::apply;
    }

    /**
     * Helper method to turn this operation into an {@link AstNode}, using op() and info()
     * @param args the parameters this operation takes.
     * @return a node in an Abstract Syntax Tree.
     */
    default AstNode createNode(Object... args){
        return new AstNode(op(), info(), Arrays.asList(args));
    }

    /**
     * An identity function (input = output)
     * @return a new {@link UnaryOperation}
     * @param <T> the type of the argument and output
     */
    static <T> Identity<T> identity() {
        return new Identity<>();
    }

    /**
     * Chain two operations together: first this operation, then the other operation.
     * @param other the later operation
     * @return a new (combined) operation
     */
    default UnaryOperation<T> then(UnaryOperation<T> other) {
        return new Then<>(this, other);
    }

    /**
     * An operation that does nothing.
     * @param <T> argument type
     */
    class Identity<T> implements UnaryOperation<T> {

        public Identity() {}

        @Override
        public String op() {
            return "identity";
        }

        @Override
        public Map<String, Object> info() {
            return Map.of();
        }

        @Override
        public AstNode ast() {
            return createNode();
        }

        @Override
        public T apply(T x) {
            return x;
        }
    }

    /**
     * First apply operation a, then apply operation b on the result.
     * @param <T> argument type
     */
    class Then<T> implements UnaryOperation<T> {

        private final UnaryOperation<T> a, b;

        public Then(UnaryOperation<T> a, UnaryOperation<T> b) {
            Objects.requireNonNull(a);
            Objects.requireNonNull(b);
            this.a = a;
            this.b = b;
        }

        @Override
        public String op() {
            return "then";
        }

        @Override
        public Map<String, Object> info() {
            return Map.of();
        }

        @Override
        public AstNode ast() {
            return createNode(a.ast(), b.ast());
        }

        @Override
        public T apply(T x) {
            return b.apply(a.apply(x));
        }
    }
}
