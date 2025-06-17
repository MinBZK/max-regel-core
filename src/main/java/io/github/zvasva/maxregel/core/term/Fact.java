package io.github.zvasva.maxregel.core.term;

import io.github.zvasva.maxregel.core.process.rule.Rule;
import io.github.zvasva.maxregel.core.process.rule.Rules;
import io.github.zvasva.maxregel.util.Collections;

import java.util.*;
import java.util.stream.Collectors;

import static io.github.zvasva.maxregel.core.process.MaxRegelException.requireNonNullArg;


/**
 * A fact is a container for a {@link Term}, with extra metadata.
 */
public class Fact {

    private final Term term;
    private Map<String, Object> info; // todo: guarantee mutable?
    private List<Rule> rules;
    private int epoch = 0;

    public static List<Fact> wrapInFacts (List<AbstractTerm> terms) {
        return terms.stream().map(Fact::new).collect(Collectors.toList());
    }

    public Fact(Term term) {
        this(term, new LinkedHashMap<>());
    }

    public Fact(Term term, Map<String, Object> info) {
        this.term = requireNonNullArg(term, "term");
        this.info = info;
        this.rules = new ArrayList<>(0);
        this.epoch = 0;
    }

    public Object get(String key) {
        return term.get(key);
    }

    public Term getTerm() {
        return term;
    }

    public List<Rule> getRules() {
        return rules;
    }

    public Fact setRules(List<Rule> rules) {
        this.rules = rules;
        return this;
    }

    public Map<String, Object> getInfo() {
        return info;
    }

    public Fact setInfo(Map<String, Object> info) {
        this.info = info;
        return this;
    }

    public int getEpoch() {
        return epoch;
    }

    public Fact setEpoch(int epoch) {
        this.epoch = epoch;
        return this;
    }

    public Fact union(Fact other) {
        Fact union = new Fact(Terms.union(term, other.term));
        union.info = Collections.merge(this.info, other.info);
        union.rules   = Collections.concat(this.rules, other.rules);
        union.epoch   = Math.max(this.epoch, other.epoch);
        return union;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Fact fact)) return false;
        return Objects.equals(term, fact.term);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(term);
    }

    @Override
    public String toString() {
        return "Fact{" +
                "term=" + term +
                ", info=" + info +
                ", rules=" + rules.stream().map(Rules::toString).collect(Collectors.joining("; ")) +
                ", epoch=" + epoch +
                '}';
    }
}
