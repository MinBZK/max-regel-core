package io.github.zvasva.maxregel.core.process.factoperation;

import io.github.zvasva.maxregel.core.factset.FactSet;
import io.github.zvasva.maxregel.core.process.AstNode;
import io.github.zvasva.maxregel.core.process.UnaryOperation;
import io.github.zvasva.maxregel.core.process.rule.AbstractRule;
import io.github.zvasva.maxregel.core.term.Fact;
import io.github.zvasva.maxregel.core.term.MapTerm;
import io.github.zvasva.maxregel.core.term.Terms;
import io.github.zvasva.maxregel.core.term.Term;

import java.util.Map;

/**
 * Given a date field within a {@link Term}, add an age field.
 */
public class RenameField extends AbstractRule {
    private final String oldName;
    private final String newName;

    public RenameField(String oldName, String newName) {
        this.oldName = oldName;
        this.newName = newName;
    }

    @Override
    public String op() {
        return "rename_field";
    }

    public UnaryOperation<Fact> factOperation() {
        return UnaryOperation.of( arg -> {
            Map<String, Object> map = Terms.asMap(arg.getTerm());

            map.put(newName, map.get(oldName));
            map.remove(oldName);

            return new Fact(new MapTerm(map), arg.getInfo());
        });
    }

    @Override
    public FactSet apply(FactSet arg) {
        return arg.addFactOperation(factOperation());
    }

    @Override
    public AstNode ast() {
        return createNode(oldName, newName);
    }
}
