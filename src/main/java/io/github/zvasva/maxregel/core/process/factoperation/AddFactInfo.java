package io.github.zvasva.maxregel.core.process.factoperation;

import io.github.zvasva.maxregel.core.factset.FactSet;
import io.github.zvasva.maxregel.core.process.AstNode;
import io.github.zvasva.maxregel.core.process.UnaryOperation;
import io.github.zvasva.maxregel.core.process.rule.AbstractRule;
import io.github.zvasva.maxregel.core.term.Fact;
import io.github.zvasva.maxregel.util.Collections;

import java.util.Map;

/**
 * Merges new info into a fact.
 * @author Arvid Halma
 */
public class AddFactInfo extends AbstractRule {

    private Map<String, Object> newInfo;

    public AddFactInfo(Map<String, Object> newInfo) {
        this.newInfo = newInfo;
    }

    @Override
    public String op() {
        return "add_fact_info";
    }

    public UnaryOperation<Fact> factOperation() {
        return UnaryOperation.of(arg -> {
            arg.setInfo(Collections.merge(arg.getInfo(), newInfo));
            return arg;
        });
    }

    @Override
    public FactSet apply(FactSet facts) {
        return facts.addFactOperation(factOperation());
    }

    @Override
    public AstNode ast() {
        return createNode(newInfo);
    }
}
