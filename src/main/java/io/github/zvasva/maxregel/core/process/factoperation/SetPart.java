package io.github.zvasva.maxregel.core.process.factoperation;

import io.github.zvasva.maxregel.core.factset.FactSet;
import io.github.zvasva.maxregel.core.process.AstNode;
import io.github.zvasva.maxregel.core.process.rule.AbstractRule;

import static io.github.zvasva.maxregel.core.process.MaxRegelException.requireNonNullArg;

/**
 * Set the part name for a factset. All facts will be available under the new name.
 * @author Arvid Halma
 */
public class SetPart extends AbstractRule {

    private String name;

    public SetPart(String name) {
        this.name = requireNonNullArg(name, "name");;
    }

    @Override
    public String op() {
        return "set_part";
    }


    @Override
    public FactSet apply(FactSet facts) {
        return facts.setPart(name);
    }

    @Override
    public AstNode ast() {
        return createNode(name);
    }
}
