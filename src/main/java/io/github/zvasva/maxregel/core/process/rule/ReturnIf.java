package io.github.zvasva.maxregel.core.process.rule;

import io.github.zvasva.maxregel.core.factset.FactSet;
import io.github.zvasva.maxregel.core.process.AstNode;
import io.github.zvasva.maxregel.core.process.predicate.Exists;
import io.github.zvasva.maxregel.core.process.predicate.Predicate;

import static io.github.zvasva.maxregel.core.process.MaxRegelException.requireNonNullArg;

/**
 * Indicate a possible early exit of the inference process.
 */
public class ReturnIf extends AbstractRule {

    Rule conditionSelect;
    Predicate<FactSet, FactSet> condition;
    Rule resultSelect;

    public ReturnIf(Rule conditionSelect) {
        this(conditionSelect, new Exists(), Rule.identity());
    }

    public ReturnIf(Rule conditionSelect, Predicate<FactSet, FactSet> condition) {
        this(conditionSelect, condition, Rule.identity());
    }

    public ReturnIf(Rule conditionSelect, Predicate<FactSet, FactSet> condition, Rule resultSelect) {
        this.conditionSelect = requireNonNullArg(conditionSelect, "conditionSelect");
        this.condition = requireNonNullArg(condition, "condition");
        this.resultSelect = requireNonNullArg(resultSelect, "resultSelect");
    }

    @Override
    public String op() {
        return "returnif";
    }

    @Override
    public AstNode ast() {
        return createNode(conditionSelect.ast(), condition.ast(), resultSelect.ast());
    }

    public boolean condition(FactSet factset) {
        return condition.test(conditionSelect.apply(factset));
    }

    public FactSet result(FactSet factset) {
        return resultSelect.apply(factset);
    }

    @Override
    public FactSet apply(FactSet factset) {
        if(condition.test(conditionSelect.apply(factset))){
            return resultSelect.apply(factset);
        }
        return factset;
    }
}

