package io.github.zvasva.maxregel.core.process.rule;

import io.github.zvasva.maxregel.core.factset.FactSet;
import io.github.zvasva.maxregel.core.factset.FactSets;
import io.github.zvasva.maxregel.core.process.AstNode;

import java.util.Objects;

/**
 * Join two parts on the given field.
 */
public class Join extends AbstractRule {
    private final Rule selectA, selectB;
    private final String fieldA, fieldB;

    public Join(Rule selectA, Rule partB, String fieldA, String fieldB) {
        Objects.requireNonNull(selectA);
        Objects.requireNonNull(partB);
        Objects.requireNonNull(fieldA);
        Objects.requireNonNull(fieldB);
        this.selectA = selectA;
        this.selectB = partB;
        this.fieldA = fieldA;
        this.fieldB = fieldB;
    }
    public Join(Rule selectA, Rule partB, String field) {
        this(selectA, partB, field, field);
    }

    @Override
    public String op() {
        return "join";
    }

    @Override
    public AstNode ast() {
        return createNode(selectA.ast(), selectB.ast(), fieldB, fieldA);
    }

    @Override
    public FactSet apply(FactSet facts) {
        return FactSets.joinOnField(selectA.apply(facts), selectB.apply(facts), fieldA, fieldB);
    }
}
