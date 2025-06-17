package io.github.zvasva.maxregel.core.process.rule;

import io.github.zvasva.maxregel.core.factset.FactSet;
import io.github.zvasva.maxregel.core.factset.MultiPartFactSet;
import io.github.zvasva.maxregel.core.process.AstNode;
import io.github.zvasva.maxregel.core.process.UnaryOperation;

import static io.github.zvasva.maxregel.core.process.MaxRegelException.requireNonNullArg;

/**
 * Update the part names by giving them all a prefix.
 */
public class NamePrefix extends AbstractRule {

    private final Rule select;
    private final String prefix;

    public NamePrefix(String prefix) {
        this(Rule.identity(), prefix);
    }

    public NamePrefix(Rule select, String prefix) {
        this.select = requireNonNullArg(select, "select");
        this.prefix = requireNonNullArg(prefix, "prefix");
    }

    @Override
    public String op() {
        return "name_prefix";
    }

    @Override
    public AstNode ast() {
        return createNode(select.ast(), prefix);
    }

    @Override
    public FactSet apply(FactSet factset) {
        final FactSet selection = select.apply(factset);
        return new MultiPartFactSet(
                selection.parts().stream().map(part -> selection.get(part).setPart(prefix + part)).toList(),
                new UnaryOperation.Identity<>()
        );
    }
}
