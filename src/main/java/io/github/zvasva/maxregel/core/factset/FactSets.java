package io.github.zvasva.maxregel.core.factset;

import io.github.zvasva.maxregel.core.process.UnaryOperation;
import io.github.zvasva.maxregel.core.process.factoperation.AddFactInfo;
import io.github.zvasva.maxregel.core.term.Fact;
import io.github.zvasva.maxregel.core.term.MapTerm;
import io.github.zvasva.maxregel.core.term.Term;
import io.github.zvasva.maxregel.core.term.Terms;
import io.github.zvasva.maxregel.util.Collections;
import io.github.zvasva.maxregel.util.Iters;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.github.zvasva.maxregel.core.factset.Empty.EMPTY;

/**
 * Factset utilities.
 *
 * @author Arvid Halma
 */
@SuppressWarnings("unused")
public class FactSets {

    /**
     * Create a new factset.
     * @param name the part name
     * @param facts the content
     * @return a factset.
     */
    public static FactSet create(String name, Stream<Fact> facts){
        return new SinglePartFactSet(Iters.iterable(facts), name);
    }

    /**
     * Create a new factset.
     * @param facts the content
     * @return a factset.
     */
    public static FactSet create(Stream<Fact> facts){
        return new SinglePartFactSet(Iters.iterable(facts));
    }

    /**
     * Create a new factset.
     * @param terms the content
     * @return a factset.
     */
    @SafeVarargs
    public static FactSet create(Map<String, Object>... terms){
        return create(Arrays.stream(terms).map(MapTerm::of).toArray(Term[]::new));
    }

    /**
     * Create a new factset.
     * @param term the content
     * @return a factset.
     */
    public static FactSet create(Term term){
        return new SinglePartFactSet(List.of(new Fact(term)), "*");
    }


    /**
     * Create a new factset.
     * @param terms the content
     * @return a factset.
     */
    public static FactSet create(Term... terms){
        return create(Arrays.stream(terms).map(Fact::new).toArray(Fact[]::new));
    }

    /**
     * Create a new factset.
     * @param name the part name
     * @param term the content
     * @return a factset.
     */
    public static FactSet create(String name, Term term){
        return new SinglePartFactSet(List.of(new Fact(term)), name);
    }

    /**
     * Create a new factset with a given name.
     * @param name the part name
     * @param terms the content
     * @return a factset.
     */
    public static FactSet create(String name, Term... terms){
        return create(name, Arrays.stream(terms).map(Fact::new).toArray(Fact[]::new));
    }

    /**
     * Create a new factset with a given name and info.
     * @param name the part name
     * @param info the info for the facts
     * @param terms the content
     * @return a factset.
     */
    public static FactSet create(String name, Map<String, Object> info, Term... terms){
        FactSet facts = create(name, Arrays.stream(terms).map(Fact::new).toArray(Fact[]::new));
        facts = facts.setFactOperation(new AddFactInfo(info).factOperation());
        return facts;
    }

    /**
     * Create a new factset.
     * @param facts the content
     * @return a factset.
     */
    public static FactSet create(Fact... facts){
        return new SinglePartFactSet(Arrays.asList(facts));
    }

    /**
     * Create a new factset with a given name.
     * @param name the part name
     * @param facts the content
     * @return a factset.
     */
    public static FactSet create(String name, Fact... facts){
        return new SinglePartFactSet(Arrays.asList(facts), name);
    }

    /**
     * Create a new factset with a given name.
     * @param name the part name
     * @param facts the content
     * @return a factset.
     */
    public static FactSet create(String name, List<Fact> facts) {
        return new SinglePartFactSet(facts, name);
    }


    public static FactSet create(FactSet ... facts) {
        return new MultiPartFactSet(Arrays.asList(facts), UnaryOperation.identity());
    }

    /**
     * Create a FactSet with a single Fact with Term containing a single value.
     * @param val the constant value to wrap
     * @param fieldName name of the field
     * @return a new FactSet
     */
    public static FactSet cnst(String fieldName, Object val){
        return create(MapTerm.of(fieldName, val));
    }

    /**
     * Create a FactSet with a single Fact with Term containing a single value.
     * @param val the constant value to wrap
     * @return a new FactSet
     */
    public static FactSet cnst(Object val){
        return create(MapTerm.of("x", val));
    }

    /**
     * Create a FactSet with a single Fact with Term containing a single value.
     * @param partName the part name of the factset
     * @param val the constant value to wrap
     * @return a new FactSet
     */
    public static FactSet cnst(String partName, String fieldName, Object val){
        return create(partName, MapTerm.of(fieldName, val));
    }

    /**
     * Combine all factsets into a single one
     * @param factSets the separate factsets
     * @return a single new factset
     */
    public static FactSet union(FactSet ... factSets) {
        return Arrays.stream(factSets).reduce(EMPTY, FactSet::union);
    }

    /**
     * get the first fact of a factset.
     * @param facts the factset
     * @return the first fact if exists, or null otherwise.
     */
    public static Fact first(FactSet facts) {
        return Iters.first(facts);
    }

    /**
     * get the first fact of a factset.
     * @param facts the factset
     * @return the first fact if exists, or null otherwise.
     */
    public static Term firstTerm(FactSet facts) {
        return Iters.first(facts).getTerm();
    }

    /**
     * get the value of the term from the first fact.
     * @param facts the factset
     * @param field the Term's field name
     * @param defaultValue the value returned when there is no data (fact or field)
     * @return the first fact if exists.
     */
    public static Object value(FactSet facts, String field, Object defaultValue) {
        Fact fact = Iters.first(facts);
        if (fact == null)
            return defaultValue;
        Term term = fact.getTerm();
        if (term.has(field)) {
            Object value = term.get(field);
            return value == null ? defaultValue : value;
        } else {
            return defaultValue;
        }
    }

    /**
     * get the value of the term from the first fact.
     * @param facts the factset
     * @return the first fact if exists.
     */
    public static Object value(FactSet facts) {
        Fact fact = Iters.first(facts);
        if (fact == null)
            return null;
        return Terms.first(fact.getTerm());
    }

    /**
     * get the value of the term from the first fact.
     * @param facts the factset
     * @param defaultValue the value returned when there is no data (fact or field)
     * @return the first fact if exists.
     */
    public static double value(FactSet facts, double defaultValue) {
        Object v = value(facts);
        return v == null ? defaultValue : (double)v;
    }

    /**
     * get the value of the term from the first fact.
     * @param facts the factset
     * @param defaultValue the value returned when there is no data (fact or field)
     * @return the first fact if exists.
     */
    public static int value(FactSet facts, int defaultValue) {
        Object v = value(facts);
        return v == null ? defaultValue : (int)v;
    }

    /**
     * get the value of the term from the first fact.
     * @param facts the factset
     * @param field the Term's field name
     * @return the first fact if exists.
     */
    public static Object value(FactSet facts, String field) {
        return value(facts, field, (Object)null);
    }

    /**
     * get the value of the term from the first fact.
     * @param facts the factset
     * @param field the Term's field name
     * @param defaultValue the value returned when there is no data (fact or field)
     * @return the first fact if exists.
     */
    public static double value(FactSet facts, String field, double defaultValue) {
        return ((Number)value(facts, field, (Object)defaultValue)).doubleValue();
    }

    /**
     * get the value of the term from the first fact.
     * @param facts the factset
     * @param field the Term's field name
     * @param defaultValue the value returned when there is no data (fact or field)
     * @return the first fact if exists.
     */
    public static int value(FactSet facts, String field, int defaultValue) {
        return ((Number)value(facts, field, (Object)defaultValue)).intValue();
    }


    /**
     * get the value of the term from the first fact.
     * @param facts the factset
     * @param field the Term's field name
     * @param defaultValue the value returned when there is no data (fact or field)
     * @return the first fact if exists.
     */
    public static long value(FactSet facts, String field, long defaultValue) {
        return ((Number)value(facts, field, (Object)defaultValue)).longValue();
    }

    /**
     * get the value of the term from the first fact.
     * @param facts the factset
     * @param field the Term's field name
     * @param defaultValue the value returned when there is no data (fact or field)
     * @return the first fact if exists.
     */
    public static String value(FactSet facts, String field, String defaultValue) {
        return (String)value(facts, field, (Object)defaultValue);
    }

    /**
     * Groups the {@link FactSet} by a specified field.
     *
     * @param facts the initial factset.
     * @param field the field to group by.
     * @return a new FactSet where facts are grouped according to the specified field.
     */
    public static FactSet groupByField(FactSet facts, String field) {
        return facts.group(f -> Objects.toString(f.get(field)));
    }

    /**
     * Joins the current {@link FactSet} with another {@link FactSet} based on a specified field.
     *
     * @param a the first FactSet.
     * @param b the other FactSet to join with.
     * @param field the field to join on.
     * @return a new FactSet representing the join of both sets.
     */
    public static FactSet joinOnField(FactSet a, FactSet b, String field) {
        return joinOnField(a, b, field, field);
    }

    /**
     * Joins the current {@link FactSet} with another {@link FactSet} based on a specified field.
     *
     * @param a the first FactSet.
     * @param b the other FactSet to join with.
     * @param aField the field to join on in a.
     * @param bField the field to join on in b.
     * @return a new FactSet representing the join of both sets.
     */
    public static FactSet joinOnField(FactSet a, FactSet b, String aField, String bField) {
        Function<Fact, String> onA = f -> Objects.toString(f.get(aField));
        Function<Fact, String> onB = f -> Objects.toString(f.get(bField));
        return a.join(b, onA, onB);
    }

    /**
     * A multiline string representation of a factset, only showing the {@link Term}s of the {@link Fact}s.
     * @param facts the factset
     * @return text
     */
    public static String toString(FactSet facts) {
        return Iters.stream(facts.parts()).map(part ->
                        "  " + part + " = " + Iters.stream(facts.get(part)).map(f -> "    " + Terms.asMap(f.getTerm())).collect(Collectors.joining(",\n", "[\n", "\n  ]")))
                .collect(Collectors.joining(",\n", "{\n", "\n}"));
    }

    /**
     * Derive each part's type of Terms.
     * @param facts the factset.
     * @return a facset with one term per part, showing the Term's value types.
     */
    public static FactSet schema(FactSet facts){
        return new MultiPartFactSet(facts.parts().stream().map(part -> {
            Fact fact = first(facts.get(part));
            if(fact != null) {
                Map<String, Object> term = Terms.asMap(fact.getTerm());
                Map<String, Object> typeTerm = Collections.mapValues(term, v -> v == null ? "null" : v.getClass().getSimpleName());
                return FactSets.create(part, new MapTerm(typeTerm));
            } else {
                return EMPTY;
            }
        }).toList(), UnaryOperation.identity());
    }

    /**
     * A multiline string representation of a factset, showing the full {@link Fact} objects.
     * @param facts the factset
     * @return text
     */
    public static String toStringFull(FactSet facts) {
        return Iters.stream(facts.parts()).map(part ->
                        "  " + part + " = " + Iters.stream(facts.get(part)).map(f -> "    " + f).collect(Collectors.joining(",\n", "[\n", "\n  ]")))
                .collect(Collectors.joining(",\n", "{\n", "\n}"));
    }

    /**
     * Get a "column" of values from a factset.
     * @param facts the factset
     * @param field the column name
     * @return a list of values
     */
    public static List<Object> getField(FactSet facts, String field) {
        return facts.stream().map(f -> f.get(field)).toList();
    }

    /**
     * Check if two factsets both have the same part names (possibly with different content).
     * @param a first factset
     * @param b second factset
     * @return true if equal by part names.
     */
    public static boolean equalParts(FactSet a, FactSet b){
        return a.parts().equals(b.parts());
    }

    /**
     * Pick certain parts from a factset
     * @param facts the original factset
     * @param parts the part names
     * @return a new subset factset
     */
    public static FactSet selectParts(FactSet facts, Collection<String> parts) {
        FactSet result = EMPTY;
        for (String part : parts) {
            result = result.union(facts.get(part));
        }
        return result;
    }

    /**
     * Keeps parts of a that also occur in b.
     * @param a first factset
     * @param b second factset
     * @return a subset of a
     */
    public static FactSet partIntersection(FactSet a, FactSet b) {
        return selectParts(a, Collections.intersection(a.parts(), b.parts()));
    }

    /**
     * The (asymetric) difference between two facsets. All elements of the second collection are removed from the first.
     * @param a first factset
     * @param b second factset
     * @return a subset of a
     */
    public static FactSet partDifference(FactSet a, FactSet b) {
        FactSet result = a;
        for (String bPart : b.parts()) {
            result = result.remove(bPart);
        }
        return result;
    }
}
