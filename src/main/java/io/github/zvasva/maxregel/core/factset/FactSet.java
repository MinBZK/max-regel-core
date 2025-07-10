package io.github.zvasva.maxregel.core.factset;

import io.github.zvasva.maxregel.core.process.UnaryOperation;
import io.github.zvasva.maxregel.core.process.predicate.Predicate;
import io.github.zvasva.maxregel.core.term.Fact;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * FactSet represents a collection of {@link Fact} objects and provides methods for
 * deriving and querying this collection. FactSets are immutable: each operation creates a new instance.
 * The container can have multiple parts, distinguished by a name.
 * <p>
 * Example:
 * <pre>
 *  | part   | facts                             |
 *  | ------ | --------------------------------- |
 *  | person | {name: "John", age: 42}           |
 *  |        | {name: "Mary", age: 63}           |
 *  | cars   | {brand: "Subaru", color: "white"} |
 *  |        | {brand: "Skoda", color: "white"}  |
 *  |        | {brand: "BMW", color: "black"}    |
 *  </pre>
 *
 * @author Arvid Halma
 */
public interface FactSet extends Iterable<Fact> {

    /**
     * A function that takes the original fact and changes it.
     * @return function object
     */
    UnaryOperation<Fact> factOperation();

    /**
     * Set new function that takes the original fact and changes it.
     * @param operation the operation to set
     * @return a new FactSet (shallow) copy
     */
    FactSet setFactOperation(UnaryOperation<Fact> operation);

    /**
     * Add new function in addition to the existing {@link #factOperation()}, that takes the original fact and changes it.
     * @param operation the operation to add
     * @return a new FactSet (shallow) copy
     */
    default FactSet addFactOperation(UnaryOperation<Fact> operation){
        return setFactOperation(factOperation().then(operation));
    }

    /**
     * Retrieve {@link Fact}s from this {@link FactSet} that are transformed first by applying {@link #factOperation()}.
     *
     * @return an Iterator over elements of type Fact.
     */
    @Override
    Iterator<Fact> iterator();

    /**
     * Checks the existense of a specific part.
     *
     * @param part the name of the part to check.
     * @return true if there is a pat, else false.
     */
    boolean has(String part);

    /**
     * Retrieves a specific part of the {@link FactSet} by name.
     *
     * @param part the name of the part to retrieve.
     * @return the corresponding FactSet for the given part name.
     */
    FactSet get(String part);

    /**
     * Provides part names contained within the {@link FactSet}.
     *
     * @return an Iterable of part names.
     */
    Set<String> parts();

    /**
     * Overwrite the part names contained within the {@link FactSet}.
     * @param newName the new name for the part.
     * @return the factset (shallow) copy with a new name
     */
    FactSet setPart(String newName);

    /**
     * Get plain a {@link Map} where the key is the part name and the values are a list of facts.
     *
     * @return a new map.
     */
    Map<String, List<Fact>> asMap();

        /**
         * Removes a specific part of the FactSet by name.
         *
         * @param part the name of the part to remove
         * @return the updated FactSet after the specified part has been removed
         */
    FactSet remove(String part);

    /**
     * Filters the {@link FactSet} using the provided predicate.
     *
     * @param predicate the predicate to apply for filtering.
     * @return a new FactSet containing only the elements that match the predicate.
     */
    FactSet filter(Predicate<Fact, FactSet> predicate);

    /**
     * Groups the {@link FactSet} by the specified function.
     *
     * @param by the function to determine the grouping key for each fact.
     * @return a new FactSet where facts are grouped according to the specified key.
     */
    FactSet group(Function<Fact, String> by);

    /**
     * Joins the current {@link FactSet} with another {@link FactSet} based on the specified key functions.
     *
     * @param other the other FactSet to join with.
     * @param leftOn the function to determine the join key for the current FactSet.
     * @param rightOn the function to determine the join key for the other FactSet.
     * @return a new FactSet representing the join of both sets.
     */
    FactSet join(FactSet other, Function<Fact, String> leftOn, Function<Fact, String> rightOn);

    /**
     * Returns a new {@link FactSet} containing only unique elements from the original set.
     *
     * @return a distinct FactSet.
     */
    FactSet distinct();

    /**
     * Combines the current {@link FactSet} with another {@link FactSet}.
     *
     * @param other the other FactSet to combine with.
     * @return a new FactSet that includes all elements from both sets.
     */
    FactSet union(FactSet other);

    /**
     * Returns a new {@link FactSet} containing only the elements that are present in both the current
     * and the specified sets.
     *
     * @param other the other FactSet to intersect with.
     * @return an intersected FactSet.
     */
    FactSet intersection(FactSet other);

    /**
     * Returns a sequential {@link Stream} with the collection of {@link Fact} elements as its source.
     *
     * @return a Stream of Fact elements.
     */
    Stream<Fact> stream();

    /**
     * Returns the number of elements in the {@link FactSet}.
     *
     * @return the size of the FactSet.
     */
    long size();

    /**
     * Checks if the {@link FactSet} is empty.
     *
     * @return {@code true} if the FactSet is empty; {@code false} otherwise.
     */
    boolean isEmpty();

    /**
     * Checks if any facts in the {@link FactSet} match the given predicate.
     *
     * @param predicate the predicate to apply.
     * @return {@code true} if any facts match the predicate; {@code false} otherwise.
     */
    boolean any(Predicate<Fact, FactSet> predicate);

    /**
     * Checks if all facts in the {@link FactSet} match the given predicate.
     *
     * @param predicate the predicate to apply.
     * @return {@code true} if all facts match the predicate; {@code false} otherwise.
     */
    boolean all(Predicate<Fact, FactSet> predicate);

}
