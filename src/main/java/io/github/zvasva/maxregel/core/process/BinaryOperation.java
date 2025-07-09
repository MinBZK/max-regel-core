package io.github.zvasva.maxregel.core.process;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BinaryOperator;

/**
 * Represents an operation upon two operands of the same type, producing a result of the same type as the operands.
 * <p>
 * Why not use {@link BinaryOperator} instead? That is a pure function
 * and can't do much else than being applied. This version allows for mapping to an
 * abstract syntax tree (AST). And it carries arbitrary meta-data in {@link #info()}.
 *
 * @author Arvid Halma
 * @param <T> the arguments and result type
 */
public interface BinaryOperation<T> {

    /**
     * An identifier/operator name.
     * @return name
     */
    String op();

    /**
     * Arbitrary meta data.
     * @return a map of key-value pairs
     */
    Map<String, Object> info();


    /**
     * Apply the operation to two arguments.
     * @param a first argument
     * @param b second argument
     * @return result of the operation
     */
    T apply(T a, T b);

    /**
     * Predicate as abstract syntax tree.
     * @return s-expression
     */
    AstNode ast();

    /**
     * Convert this operation to a plain Java {@link BinaryOperator}.
     * This is useful for passing the operation to a Java function that expects a {@link BinaryOperator}.
     * @return a plain Java binary operator
     */
    default BinaryOperator<T> asJavaBinaryOperation() {
        return BinaryOperation.this::apply;
    }

    /**
     * Create an anonymous {@link BinaryOperation} (no op() name, no meaningful AST.
     * @param javaOp the plain Java version of the function
     * @param <T> The type of the arguments and result
     * @return a wrapped java function
     */
    static <T> BinaryOperation<T> fromJavaBinaryOperator(BinaryOperator<T> javaOp) {
        return new BinaryOperation<T>() {
            @Override
            public String op() {
                return "?";
            }

            @Override
            public Map<String, Object> info() {
                return new LinkedHashMap<>(0);
            }

            @Override
            public T apply(T a, T b) {
                return javaOp.apply(a, b);
            }

            @Override
            public AstNode ast() {
                return createNode();
            }
        };
    }


    default AstNode createNode(Object... args){
        return new AstNode(op(), info(), List.of(args));
    }
}
