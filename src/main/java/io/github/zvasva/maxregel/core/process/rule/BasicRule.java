package io.github.zvasva.maxregel.core.process.rule;

import io.github.zvasva.maxregel.core.factset.FactSet;
import io.github.zvasva.maxregel.core.process.AstNode;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.UnaryOperator;

import static io.github.zvasva.maxregel.core.process.MaxRegelException.requireNonNullArg;

/**
 * BasicRule is a concrete implementation of the Rule interface that applies a specified
 * transformation to a FactSet.
 * <p>
 * It encapsulates an identification string, additional information as a map,
 * and a UnaryOperator to perform the rule application.
 */
public class BasicRule extends AbstractRule {

    private final String op;
    private final Map<String, Object> info;
    private final AstNode ast;
    private final UnaryOperator<FactSet> apply;


    public BasicRule(UnaryOperator<FactSet> apply) {
        this(apply, "anonymousRule", new LinkedHashMap<>(0), new AstNode("anonymousRule", Map.of(), List.of()));
    }

    public BasicRule(UnaryOperator<FactSet> apply, String op) {
        this(apply, op, new LinkedHashMap<>(0), new AstNode(op, Map.of(), List.of()));
    }

    public BasicRule(String op, Map<String, Object> info, List<Object> args) {
        this.op = requireNonNullArg(op, "op");
        this.info = requireNonNullArg(info, "info");
        this.apply = UnaryOperator.identity();
        this.ast = new AstNode(op, info, args);
    }

    public BasicRule(UnaryOperator<FactSet> apply, String op, Map<String, Object> info, AstNode ast) {
        this.apply = requireNonNullArg(apply, "apply");
        this.op = requireNonNullArg(op, "op");
        this.info = new LinkedHashMap<>(requireNonNullArg(info, "info"));
        this.ast = requireNonNullArg(ast, "ast");
    }

    @Override
    public String op() {
        return op;
    }

    @Override
    public Map<String, Object> info() {
        return info;
    }

    @Override
    public AstNode ast() {
        return ast;
    }

    @Override
    public FactSet apply(FactSet factset) {
        return apply.apply(factset);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof BasicRule basicRule)) return false;
        return Objects.equals(op, basicRule.op);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(op);
    }

    @Override
    public String toString() {
        return op;
    }
}

