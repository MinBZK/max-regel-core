package io.github.zvasva.maxregel.core.factset;

import io.github.zvasva.maxregel.core.process.UnaryOperation;
import io.github.zvasva.maxregel.core.process.predicate.Predicate;
import io.github.zvasva.maxregel.core.term.Fact;
import io.github.zvasva.maxregel.util.Iters;

import java.util.*;
import java.util.function.Function;
import java.util.function.UnaryOperator;

import static io.github.zvasva.maxregel.core.factset.Empty.EMPTY;
import static io.github.zvasva.maxregel.util.Iters.concat;

/**
 * Multi Part Factset.
 * The implementation uses an (in memory) {@link Map} to segment facts by part (key).
 * It is the most basic, generic implementation of a {@link FactSet}.
 *
 * @author Arvid Halma
 */
public class MultiPartFactSet extends AbstractFactSet {

    private final Map<String, FactSet> map;

    public MultiPartFactSet(Iterable<? extends FactSet> factsets, UnaryOperation<Fact> factOperation) {
        Objects.requireNonNull(factsets);
        Objects.requireNonNull(factOperation);
        this.map = new LinkedHashMap<>();
        this.factOperation = factOperation;

        factsets.forEach(factset -> factset.parts().forEach(name -> {
           map.computeIfAbsent(name, k -> EMPTY);
           map.put(name, map.get(name).union(factset));
        }));
    }

    public MultiPartFactSet(FactSet factset){
        this(
                Iters.stream(factset.parts()).map(part -> factset.get(part).setPart(part)).toList(),
                new UnaryOperation.Identity<>()
        );
    }

    private MultiPartFactSet(Map<String, FactSet> map, UnaryOperation<Fact> factOperation) {
        this.map = map;
        this.factOperation = factOperation;
    }

    protected static MultiPartFactSet fromMapOfFacts(Map<String, ? extends Collection<Fact>> map, final UnaryOperation<Fact> factOperation) {
        MultiPartFactSet result = new MultiPartFactSet(List.of(), factOperation);
        map.forEach((key, value) -> result.map.put(key, new SinglePartFactSet(value, key)));
        return result;
    }

    private FactSet mapParts(UnaryOperator<FactSet> partMapping) {
        return isEmpty() ? this : new MultiPartFactSet(Iters.stream(parts())
                .map(part -> partMapping.apply(get(part)).setPart(part)).toList(), factOperation());
    }

    @Override
    public Iterator<Fact> iterator() {
        return new Iters.MappingIterator<>(concat(map.values().stream().map(FactSet::iterator)), factOperation().asJavaUnaryOperator());
    }

    @Override
    public FactSet setFactOperation(UnaryOperation<Fact> operation) {
        return new MultiPartFactSet(map, operation);
    }

    @Override
    public boolean has(String part) {
        return map.containsKey(part);
    }

    @Override
    public FactSet get(String part) {
        return map.getOrDefault(part, EMPTY);
    }

    @Override
    public Set<String> parts() {
        return map.keySet();
    }

    @Override
    public FactSet setPart(String newName) {
        return new SinglePartFactSet(this, newName, false, factOperation());
    }

    @Override
    public long size() {
        return map.values().stream().mapToLong(FactSet::size).sum();
    }

    @Override
    public FactSet filter(Predicate<Fact, FactSet> predicate) {
//        return new MultiPartFactSet(Maps.mapValues(map, fs -> fs.filter(predicate)));
        return mapParts(fs -> fs.filter(predicate));
    }

    @Override
    public FactSet group(Function<Fact, String> by) {
        return new SinglePartFactSet(this, "*", false, factOperation()).group(by);
    }

    @Override
    public FactSet join(FactSet other, Function<Fact, String> leftOn, Function<Fact, String> rightOn) {
//        return new MultiPartFactSet(Maps.mapValues(map, fs -> fs.join(other, leftOn, rightOn)));
        return mapParts(fs -> fs.join(other, leftOn, rightOn));
    }

    @Override
    public FactSet distinct() {
        // make sure to us get(part) in order to have all facts from all factsets with that name.
        return mapParts(FactSet::distinct);
    }

    @Override
    public FactSet intersection(FactSet other) {
        return mapParts(fs -> fs.intersection(other));
    }

    @Override
    public boolean isEmpty() {
        return map.values().stream().allMatch(FactSet::isEmpty);
    }

}
