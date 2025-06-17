package io.github.zvasva.maxregel.core.factset;

import io.github.zvasva.maxregel.core.process.UnaryOperation;
import io.github.zvasva.maxregel.core.process.predicate.Predicate;
import io.github.zvasva.maxregel.core.term.Fact;
import io.github.zvasva.maxregel.util.Iters;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;

import static io.github.zvasva.maxregel.core.factset.Empty.EMPTY;

/**
 * The Concat class extends AbstractFactSet to provide a composite FactSet
 * that aggregates two specified FactSets into one.
 * <p>
 * This class allows operations on the combined data from two FactSets to be
 * performed as though they were a single FactSet, but in fact leaving them untouched.
 * This make concatenation an operation in constant time and space, i.e. O(1).
 *
 *  @author Arvid Halma
 */
public class Concat extends AbstractFactSet {
    private final FactSet a, b;

    public Concat(FactSet a, FactSet b) {
        Objects.requireNonNull(a);
        Objects.requireNonNull(b);
        this.a = a;
        this.b = b;
    }

    /*
       The following methods use the underlying factsets to do factOperation().
       But the AbstractFactSet can also handle the overarching operation.
       // todo: think about the consequences.
     */
    /*@Override
    public UnaryOperation<Fact> factOperation() {
        return new UnaryOperation.Then<>(a.factOperation(), b.factOperation());
    }

    @Override
    public FactSet setFactOperation(UnaryOperation<Fact> operation) {
        a.setFactOperation(operation);
        b.setFactOperation(operation);
        return this;
    }

    @Override
    public FactSet addFactOperation(UnaryOperation<Fact> operation) {
        a.addFactOperation(operation);
        b.addFactOperation(operation);
        return this;
    }*/

    @Override
    public FactSet setFactOperation(UnaryOperation<Fact> operation) {
        return new Concat(a.setFactOperation(operation), b.setFactOperation(operation));
    }

    @Override
    public Iterator<Fact> iterator() {
        return new Iters.MappingIterator<>(Iters.concatIterator(a.iterator(), b.iterator()), factOperation().asJavaUnaryOperator());
    }

    @Override
    public long size() {
        return a.size() + b.size();
    }

    @Override
    public boolean isEmpty() {
        return a.isEmpty() && b.isEmpty();
    }

    @Override
    public boolean has(String part) {
        return b.has(part) || a.has(part); // checking b first, then a, speeds up ~10%
    }

    @Override
    public FactSet get(String part) {
        // check basic cases to prevent unnecessary object pollution
        // checking b first, then a, speeds up ~10%
        if(!b.has(part)){
            return a.get(part);
        }
        if(!a.has(part)){
            return b.get(part);
        }
        return new SinglePartFactSet(Iters.concat(a.get(part), b.get(part)), part, false, this.factOperation());
    }

    @Override
    public Set<String> parts() {
        Set<String> result = new HashSet<>();
        result.addAll(a.parts());
        result.addAll(b.parts());
        return result;
    }

    @Override
    public FactSet setPart(String newName) {
        return new Concat(a.setPart(newName), b.setPart(newName)).setFactOperation(factOperation());
    }

    @Override
    public FactSet remove(String part) {
        boolean aHas = a.has(part);
        boolean bHas = b.has(part);
        if(!aHas && !bHas){
            return this;
        }
        return new Concat(aHas ? a.remove(part) : a, bHas ? b.remove(part) : b);
    }

    @Override
    public FactSet filter(Predicate<Fact, FactSet> predicate) {
        return a.filter(predicate).union(b.filter(predicate));
    }

    @Override
    public FactSet group(Function<Fact, String> by) {
        // return new SinglePartFactSet(this).group(by); // eager
        // todo: understand when to do lazy, when eager... (you can probably always do eager by MPFactset(lazyversion)
        return new Concat(a.group(by), b.group(by)); // lazy
    }

    @Override
    public FactSet join(FactSet other, Function<Fact, String> leftOn, Function<Fact, String> rightOn) {
        return new Concat(a.join(other, leftOn, rightOn), b.join(other, leftOn, rightOn));
    }

    @Override
    public FactSet distinct() {
//        return new Concat(a.distinct(), b.distinct());
        return new MultiPartFactSet(Iters.stream(parts()).map(part -> get(part).distinct()).toList(), this.factOperation());
    }

    @Override
    public FactSet intersection(FactSet other) {
        if(isEmpty() || other.isEmpty()) {
            return EMPTY;
        }
        return new Concat(a.intersection(other), b.intersection(other));
    }

}
