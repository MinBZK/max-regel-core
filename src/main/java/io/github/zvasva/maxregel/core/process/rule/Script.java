package io.github.zvasva.maxregel.core.process.rule;

import io.github.zvasva.maxregel.core.factset.FactSet;
import io.github.zvasva.maxregel.core.process.AstNode;
import io.github.zvasva.maxregel.core.process.Tracer;
import io.github.zvasva.maxregel.util.Collections;

import java.util.List;

import static io.github.zvasva.maxregel.core.process.MaxRegelException.requireNonNullArg;

/**
 * A script is a composition of multiple rules.
 * Each Script instance applies a series of rules to a given FactSet.
 */
public class Script extends AbstractRule {

    protected List<Rule> rules;

    public Script() {
        this.rules = List.of();
    }

    public Script(List<Rule> rules) {
        this.rules = requireNonNullArg(rules, "rules");
    }

    public List<Rule> getRules() {
        return rules;
    }

    @Override
    public String op() {
        return "script";
    }

    @Override
    public AstNode ast() {
        return createNode(rules.stream().map(Rule::ast).toArray());
    }

    @Override
    public RuleResult apply(FactSet factset, Tracer tracer) {
        return Rules.sequence(rules).apply(factset, tracer);
    }

    @Override
    public FactSet apply(FactSet factset) {
        return Rules.sequence(rules).apply(factset);
    }

    public Script append (Script script) {
        return new Script(Collections.concat(this.rules, script.getRules()));
    }

    public Script append(Rule rule) {
        return new Script(Collections.concat(this.rules, List.of(rule)));
    }

}
