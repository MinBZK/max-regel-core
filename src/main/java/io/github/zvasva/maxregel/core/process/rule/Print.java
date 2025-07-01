package io.github.zvasva.maxregel.core.process.rule;

import io.github.zvasva.maxregel.core.factset.FactSet;
import io.github.zvasva.maxregel.core.process.AstNode;
import io.github.zvasva.maxregel.util.PrettyPrint;

import static io.github.zvasva.maxregel.core.process.MaxRegelException.requireNonNullArg;

/**
 * Prints the factset to the console
 */
public class Print extends AbstractRule {

    private final Rule select;
    private final String message;

    public Print() {
        this(Rule.identity(),"");
    }

    public Print(String message) {
        this(Rule.identity(), message);
    }

    public Print(Rule select, String message) {
        this.select = requireNonNullArg(select, "select");
        this.message = requireNonNullArg(message, "message");
    }

    @Override
    public String op() {
        return "print";
    }

    @Override
    public AstNode ast() {
        return createNode(select.ast(), message);
    }

    @Override
    public FactSet apply(FactSet factset) {
        System.out.println();
        System.out.println(message);
        PrettyPrint.print(select.apply(factset));
        return factset;
    }
}
