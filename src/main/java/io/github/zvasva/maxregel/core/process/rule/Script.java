package io.github.zvasva.maxregel.core.process.rule;

import io.github.zvasva.maxregel.core.factset.FactSet;
import io.github.zvasva.maxregel.core.process.AstNode;
import io.github.zvasva.maxregel.core.process.Tracer;
import io.github.zvasva.maxregel.util.Collections;
import io.github.zvasva.maxregel.util.PrettyPrint;

import java.util.List;

import static io.github.zvasva.maxregel.core.factset.Empty.EMPTY;
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

    private static void printException(Rule r, Exception e) {
        System.err.println("The following statement: " + PrettyPrint.pretty(r));
        System.err.println("\nThrew exception: " + e.getMessage() );
        System.err.println();
        e.printStackTrace();
    }

    @Override
    public RuleResult apply(FactSet factset, Tracer tracer) {
        FactSet update = EMPTY;
        FactSet total = factset;
        for (Rule rule : rules) {
            RuleResult result = null;
            try {
                result = rule.apply(total, tracer);
            } catch (Exception e) {
                printException(rule, e);
                throw e; // rethrow
            }
            update = update.union(result.update());
            total = result.total();
        }
        return new RuleResult(update, total);
    }

    @Override
    public FactSet apply(FactSet factset) {
        FactSet result = factset;
        for (Rule rule : rules) {
            try {
                result = rule.apply(result);
            } catch (Exception e) {
                printException(rule, e);
                throw e; // rethrow
            }
        }
        return result;
    }

    public Script append (Script script) {
        return new Script(Collections.concat(this.rules, script.getRules()));
    }

    public Script append(Rule rule) {
        return new Script(Collections.concat(this.rules, List.of(rule)));
    }

}
