package io.github.zvasva.maxregel.core.process.rule;

import io.github.zvasva.maxregel.core.factset.FactSet;
import io.github.zvasva.maxregel.core.process.AstNode;
import io.github.zvasva.maxregel.core.process.predicate.Predicate;

import java.util.List;

import static io.github.zvasva.maxregel.core.process.MaxRegelException.requireNonNullArg;
import static io.github.zvasva.maxregel.core.process.rule.Rules.cnst;

/**
 * The Case Rule goes through conditions and returns a value when the first condition is met (like an if-then-else statement).
 * Once a condition is true, it will stop reading and applies the corresponding rule. If no conditions are true, it returns the defaultValue.
 */
public class FactSetCase extends AbstractRule {

    private final List<LookupEntry> lookup;
    private final Rule defaultValue;
    private final String varName; // todo remove? // for term

    public FactSetCase(List<LookupEntry> lookup, Rule defaultValue, String varName) {
        this.lookup = requireNonNullArg(lookup, "lookup");
        this.defaultValue = requireNonNullArg(defaultValue, "defaultValue");
        this.varName = requireNonNullArg(varName, "varName");
    }

    public FactSetCase(LookupEntry lookup, Rule defaultValue, String varName) {
        this.lookup = requireNonNullArg(List.of(lookup), "lookup");
        this.defaultValue = requireNonNullArg(defaultValue, "defaultValue");
        this.varName = requireNonNullArg(varName, "varName");
    }

    public FactSetCase(Predicate<FactSet, FactSet> condition, String varName) {
        this(List.of(new LookupEntry(Rule.identity(), condition, cnst(true))), cnst(false), varName);
    }

    /*public FactSetCase(Predicate<FactSet> condition, Rule thenValue, Rule defaultValue, String varName) {
        requireNonNullArg(condition, "condition");
        requireNonNullArg(thenValue, "thenValue");
        this.defaultValue = requireNonNullArg(defaultValue, "defaultValue");
        this.varName = requireNonNullArg(varName, "varName");
        lookup = new LinkedHashMap<>(1);
        lookup.put(condition, thenValue);
    }*/


    public List<LookupEntry> getLookup() {
        return lookup;
    }

    public Rule getDefaultValue() {
        return defaultValue;
    }

    @Override
    public String op() {
        return "factsetcase";
    }

    @Override
    public AstNode ast() {
        List<List<AstNode>> lookupAsListOfTuples = lookup.stream().map(
                entry -> List.of(entry.conditionSelect.ast(), entry.condition.ast(), entry.consequence.ast())
        ).toList();
        return createNode(lookupAsListOfTuples, defaultValue.ast(), varName);
    }

    @Override
    public FactSet apply(FactSet facts) {
        for (LookupEntry entry : lookup) {
            if(entry.condition.test(entry.conditionSelect.apply(facts))){
                return entry.consequence.apply(facts);
            }
        }
        return defaultValue.apply(facts);
    }

    /**
     * A single line in a case statement that reads like:
     * if condition on selection holds, return the consequence.
     * @param conditionSelect factset on which the predicate is tested.
     * @param condition the factset predicate
     * @param consequence the resulting value in case of success.
     */
    public record LookupEntry(Rule conditionSelect, Predicate<FactSet, FactSet> condition, Rule consequence) {}

}
