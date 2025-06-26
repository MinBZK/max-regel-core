package io.github.zvasva.maxregel.core.factset;

import io.github.zvasva.maxregel.core.process.UnaryOperation;
import io.github.zvasva.maxregel.core.process.predicate.Comparator;
import io.github.zvasva.maxregel.core.process.predicate.Predicate;
import io.github.zvasva.maxregel.core.term.Fact;
import io.github.zvasva.maxregel.util.Collections;
import io.github.zvasva.maxregel.util.Iters;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import static io.github.zvasva.maxregel.core.factset.Empty.EMPTY;

/**
 * Single Part Factset.
 * This {@link FactSet} is halfway there of being a fully general FactSet: it supports just a single section/part/label,
 * but otherwise satisfies all your needs. Take a look at {@link MultiPartFactSet} for a factset like a factset is meant
 * to be (but it'll use this class here and there).
 *
 * @author Arvid Halma
 */
public class SinglePartFactSet extends AbstractFactSet {
    private final Collection<Fact> facts;
    private final String name;
    private final boolean distinct;

    // Map from field name -> value -> factset
    private final Map<String, Map<Object, FactSet>> fieldIndex;

    public Map<Object, FactSet> getIndex(String fieldName)  {
        return fieldIndex.computeIfAbsent(fieldName, f -> {
            Map<Object, List<Fact>> valueMap = new HashMap<>();
            for (Fact fact : facts) {
                Object value = fact.get(fieldName);
                if(value == null) {
                    continue;
                }
                if(value instanceof Number n) {
                    value = n.doubleValue(); // int, long, double... all to double
                }
                if(!valueMap.containsKey(value)){
                    valueMap.put(value, new ArrayList<>());
                }
                valueMap.get(value).add(fact);
            }
            return Collections.mapValues(valueMap, facts -> new SinglePartFactSet(facts, name, distinct, factOperation()));
        });
    }

    public SinglePartFactSet(Iterable<Fact> facts, String name, boolean distinct, UnaryOperation<Fact> factOperation) {
        this(facts, name, distinct, factOperation, new ConcurrentHashMap<>());
    }

    private SinglePartFactSet(Iterable<Fact> facts, String name, boolean distinct, UnaryOperation<Fact> factOperation, final Map<String, Map<Object, FactSet>> fieldIndex) {
        Objects.requireNonNull(facts);
        Objects.requireNonNull(name);
        Objects.requireNonNull(factOperation);
        this.name = name;
        this.distinct = distinct;
        this.factOperation = factOperation;
        this.fieldIndex = fieldIndex;

        if (distinct) {
            if (facts instanceof Set<Fact> set){
                // already a set
               this.facts = set;
            } else {
                // convert to set, reuse
                this.facts = new HashSet<>();
                facts.forEach(this.facts::add);
            }
        } else {
            if (facts instanceof List<Fact> list){
                // already a list, reuse
                this.facts = list;
            } else {
                // convert to list
                this.facts = new ArrayList<>();
                facts.forEach(this.facts::add);
            }
        }
    }

    public SinglePartFactSet(Iterable<Fact> facts, String name) {
        this(facts, name, false, new UnaryOperation.Identity<>());
    }

    public SinglePartFactSet(Iterable<Fact> facts) {
        this(facts, "*", false, new UnaryOperation.Identity<>());
    }

    @Override
    public Iterator<Fact> iterator() {
        return new Iters.MappingIterator<>(facts.iterator(), factOperation().asJavaUnaryOperator());
    }

    @Override
    public FactSet setFactOperation(UnaryOperation<Fact> operation) {
        return new SinglePartFactSet(facts, name, distinct, operation, fieldIndex);
    }

    @Override
    public boolean has(String part) {
        return name.equals(part);
    }

    @Override
    public FactSet get(String part) {
        if(part.equals(name)) {
            return this;
        }
        return EMPTY;
    }

    @Override
    public long size() {
        return facts.size();
    }

    @Override
    public Set<String> parts() {
        return Set.of(name);
    }

    @Override
    public FactSet setPart(String newName) {
        return new SinglePartFactSet(facts, newName, distinct, factOperation, fieldIndex);
//        return new SinglePartFactSet(facts, newName, distinct, factOperation, new ConcurrentHashMap<>());
    }

    @Override
    public FactSet filter(Predicate<Fact, FactSet> predicate) {
        if(predicate instanceof Comparator cmp && "field_eq".equals(cmp.op())){
            Object y = cmp.getY();
            if(y instanceof Number n) {
                y = n.doubleValue(); // int, long, double... all to double
            }
            FactSet filtered = getIndex(cmp.getField()).getOrDefault(y, EMPTY);
            if(!filtered.isEmpty() && !filtered.has(name)) {
                // the fieldIndex is shared and the name may have been changed among instances.
                filtered = filtered.setPart(name);
            }
            return filtered;
        }
        return new SinglePartFactSet(facts.stream().filter(predicate.asJavaPredicate())::iterator, name, distinct, factOperation());
    }

    @Override
    public FactSet remove(String part) {
        return name.equals(part) ? EMPTY : this;
    }

    @Override
    public FactSet group(Function<Fact, String> by) {
        Map<String, List<Fact>> groups = new LinkedHashMap<>();
        for (Fact fact : facts) {
            String key = by.apply(fact);
            groups.computeIfAbsent(key, k -> new ArrayList<>());
            groups.get(key).add(fact);
        }
        return MultiPartFactSet.fromMapOfFacts(groups, factOperation);
    }

    @Override
    public FactSet join(FactSet other, Function<Fact, String> leftOn, Function<Fact, String> rightOn) {
        // Q: Should parts/names match in "other" factset?
        // A: No. The other should

        Map<String, List<Fact>> otherKeyMap = new LinkedHashMap<>();

        for (Fact fact : other) {
            String key = rightOn.apply(fact);
            otherKeyMap.computeIfAbsent(key, k -> new ArrayList<>());
            otherKeyMap.get(key).add(fact);
        }

        List<Fact> newFacts = new ArrayList<>();
        for (Fact fact : this) {
            String key  = leftOn.apply(fact);
            if(otherKeyMap.containsKey(key)){
                otherKeyMap.get(key).forEach(otherFact -> newFacts.add(fact.union(otherFact)));
            }
        }

        return new SinglePartFactSet(newFacts, name, false, factOperation());
    }

    @Override
    public FactSet distinct() {
        return distinct ? this : new SinglePartFactSet(this, name, true, factOperation());
    }

    @Override
    public FactSet intersection(FactSet other) {
        if(other.isEmpty())
            return EMPTY;

        return new SinglePartFactSet(Collections.union(this, other), name, distinct, factOperation());
    }
}
