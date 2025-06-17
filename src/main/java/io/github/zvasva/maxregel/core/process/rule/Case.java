package io.github.zvasva.maxregel.core.process.rule;

import io.github.zvasva.maxregel.core.factset.FactSet;
import io.github.zvasva.maxregel.core.factset.FactSets;
import io.github.zvasva.maxregel.core.process.AstNode;
import io.github.zvasva.maxregel.core.process.predicate.Predicate;
import io.github.zvasva.maxregel.core.term.Fact;

import java.util.List;

import static io.github.zvasva.maxregel.core.factset.Empty.EMPTY;
import static io.github.zvasva.maxregel.core.process.MaxRegelException.requireNonNullArg;
import static io.github.zvasva.maxregel.core.process.rule.Rules.cnst;

/**
 * The Case Rule goes through conditions and returns a value when the first condition is met (like an if-then-else statement).
 * Once a condition is true, it will stop reading and applies the corresponding rule. If no conditions are true, it returns the defaultValue.
 * The case is applied to all facts in the argument. Corresponding results are added to overall resulting factset.
 */
public class Case extends AbstractRule {

    private final Rule select;
    private final List<LookupEntry> lookup;
    private final Rule defaultValue;
    private final String varName; // for term

    public Case(List<LookupEntry> lookup, Rule defaultValue, String varName) {
        this(Rule.identity(), lookup, defaultValue, varName);
    }

    public Case(LookupEntry lookup, Rule defaultValue, String varName) {
        this(Rule.identity(), List.of(lookup), defaultValue, varName);
    }

    public Case(Rule select, List<LookupEntry> lookup, Rule defaultValue, String varName) {
        this.select = requireNonNullArg(select, "select");
        this.lookup = requireNonNullArg(lookup, "lookup");
        this.defaultValue = requireNonNullArg(defaultValue, "defaultValue");
        this.varName = requireNonNullArg(varName, "varName");
    }

    public Case(Predicate<Fact, FactSet> condition, String varName) {
        this(Rule.identity(), condition, cnst(true), cnst(false), varName);
    }

    public Case(Predicate<Fact, FactSet> condition, Rule thenValue, Rule defaultValue, String varName) {
        this(Rule.identity(), condition, thenValue, defaultValue, varName);
    }

    public Case(Rule select, Predicate<Fact, FactSet> condition, Rule thenValue, Rule defaultValue, String varName) {
        this.select = requireNonNullArg(select, "select");
        requireNonNullArg(condition, "condition");
        requireNonNullArg(thenValue, "thenValue");
        this.defaultValue = requireNonNullArg(defaultValue, "defaultValue");
        this.varName = requireNonNullArg(varName, "varName");
        lookup = List.of(new LookupEntry(condition, thenValue));
    }

    @Override
    public String op() {
        return "case";
    }

    @Override
    public AstNode ast() {
        List<List<AstNode>> lookupAsListOfTuples = lookup.stream().map(
                entry -> List.of(entry.condition.ast(), entry.consequence.ast())
        ).toList();
        return createNode(select.ast(), lookupAsListOfTuples, defaultValue.ast(), varName);
    }

    @Override
    public FactSet apply(FactSet facts) {
        facts = select.apply(facts);
        FactSet result = EMPTY;
        for (Fact fact : facts) {
            FactSet subResult = null;
            for (LookupEntry entry : lookup) {
                if(entry.condition().test(fact)){
                    subResult = entry.consequence().apply(FactSets.create(fact));
                    break;
                }
            }
            if(subResult == null) {
                subResult = defaultValue.apply(FactSets.create(fact));
            }
            result = result.union(subResult);
        }

        return result;
    }


    /**
     * A single line in a case statement that reads like:
     * if condition on a fact holds, return the consequence.
     * @param condition the factset predicate
     * @param consequence the resulting value in case of success.
     */
    public record LookupEntry(Predicate<Fact, FactSet> condition, Rule consequence) {}


}
