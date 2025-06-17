package io.github.zvasva.maxregel.core.factset;

import io.github.zvasva.maxregel.core.process.UnaryOperation;
import io.github.zvasva.maxregel.core.process.predicate.Predicate;
import io.github.zvasva.maxregel.core.term.Fact;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Empty factset (no parts, no facts).
 * It is a commonly used value, and deserves its own direct implementation for efficiency (instead of extending {@link AbstractFactSet}).
 *
 * @author Arvid Halma
 */
public class Empty implements FactSet {

    /**
     * Empty factset instance.
     */
    public static final FactSet EMPTY = new Empty();

    private Empty() {}

    @Override
    public UnaryOperation<Fact> factOperation() {
        return new UnaryOperation.Identity<>();
    }

    @Override
    public FactSet setFactOperation(UnaryOperation<Fact> ignored) {
        return this;
    }

    @Override
    public Iterator<Fact> iterator() {
        return Collections.emptyIterator();
    }

    @Override
    public boolean has(String part) {
        return false;
    }

    @Override
    public FactSet get(String part) {
        return this;
    }

    @Override
    public Set<String> parts() {
        return Set.of();
    }

    @Override
    public FactSet setPart(String ignored) {
        return this;
    }

    @Override
    public Map<String, List<Fact>> asMap() {
        return Map.of();
    }

    @Override
    public FactSet remove(String part) {
        return this;
    }

    @Override
    public FactSet filter(Predicate<Fact, FactSet> predicate) {
        return this;
    }

    @Override
    public FactSet group(Function<Fact, String> by) {
        return this;
    }

    @Override
    public FactSet join(FactSet other, Function<Fact, String> leftOn, Function<Fact, String> rightOn) {
        return this;
    }

    @Override
    public FactSet distinct() {
        return this;
    }

    @Override
    public FactSet union(FactSet other) {
        return other;
    }

    @Override
    public FactSet intersection(FactSet other) {
        return this;
    }

    @Override
    public Stream<Fact> stream() {
        return Stream.empty();
    }

    @Override
    public long size() {
        return 0;
    }

    @Override
    public boolean isEmpty() {
        return true;
    }

    @Override
    public boolean any(Predicate<Fact, FactSet> predicate) {
        return false;
    }

    @Override
    public boolean all(Predicate<Fact, FactSet> predicate) {
        return true;
    }

    @Override
    public String toString() {
        return "EMPTY";
    }

    @Override
    public int hashCode() {
        return 0;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof FactSet other)){
            return false;
        }
        return other.isEmpty();
    }
}
