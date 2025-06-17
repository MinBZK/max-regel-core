package io.github.zvasva.maxregel.core.factset;


import io.github.zvasva.maxregel.core.process.UnaryOperation;
import io.github.zvasva.maxregel.core.process.predicate.Not;
import io.github.zvasva.maxregel.core.process.predicate.Predicate;
import io.github.zvasva.maxregel.core.term.Fact;
import io.github.zvasva.maxregel.util.Iters;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;


/**
 * AbstractFactSet provides a skeleton implementation of the FactSet interface to minimize the effort required to implement this interface.
 * It uses other core functions from the FactSet interface to derive higher level/convenience methods.
 * For example:  isEmpty() can use the more fundamental iterator() method to find out if there is anything to iterate at all.
 * <p>
 * This class provides default implementations for various methods such as any, all, stream, size, isEmpty, union... and equals, hashCode and toString.
 * @author Arvid Halma
 */
public abstract class AbstractFactSet implements FactSet {

    protected UnaryOperation<Fact> factOperation = new UnaryOperation.Identity<>();

    @Override
    public UnaryOperation<Fact> factOperation() {
        return factOperation;
    }

    @Override
    public FactSet remove(String part) {
        if(!parts().contains(part))
            return this;
        List<FactSet> remainingParts = parts().stream().filter(p -> !p.equals(part)).map(this::get).toList();
        return new MultiPartFactSet(remainingParts, factOperation());
    }

    @Override
    public Stream<Fact> stream() {
        return Iters.stream(this);
    }

    @Override
    public long size() {
        // O(n) implementations. It is something, but please override.
        return Iters.count(this);
    }

    public Map<String, List<Fact>> asMap() {
        LinkedHashMap<String, List<Fact>> result = new LinkedHashMap<>();
        for (String part : parts()) {
            result.put(part, get(part).stream().toList());
        }
        return result;
    }

    @Override
    public boolean isEmpty() {
        return Iters.isEmpty(this);
    }

    @Override
    public boolean has(String part) {
        return parts().contains(part);
    }

    @Override
    public boolean any(Predicate<Fact, FactSet> predicate){
        return stream().anyMatch(predicate::test);
    }

    @Override
    public boolean all(Predicate<Fact, FactSet> predicate){
        return stream().noneMatch(new Not<>(predicate)::test);
    }

    @Override
    public FactSet union(FactSet other) {
        if(isEmpty()) {
            return other;
        } else if (other.isEmpty()) {
            return this;
        }
        return new Concat(this, other);
    }

    @Override
    public String toString() {
        return FactSets.toString(this);
    }

    @Override
    public int hashCode() {
        return (int)size();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof FactSet other)){
            return false;
        }
        if(isEmpty() && other.isEmpty()) {
            return true;
        }
        return Iters.toSet(this).equals(Iters.toSet(other));
    }
}