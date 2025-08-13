package io.github.zvasva.maxregel.core.process.predicate;

import io.github.zvasva.maxregel.core.factset.FactSet;
import io.github.zvasva.maxregel.core.factset.FactSets;
import io.github.zvasva.maxregel.core.process.MaxRegelException;
import io.github.zvasva.maxregel.core.term.Fact;
import io.github.zvasva.maxregel.core.term.MapTerm;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.github.zvasva.maxregel.core.process.rule.Rules.from;
import static io.github.zvasva.maxregel.core.process.rule.Rules.select;
import static org.junit.jupiter.api.Assertions.*;

/**
 * The PredicateTest class contains unit tests for evaluating different predicate logic
 * against Fact objects with various field values using the Predicate predicates.
 */
public class PredicateTest {

    static Comparator eq(String field, Object value) {
        return new Comparator.FieldEq(field, value);
    }

    static Comparator gt(String field, Object value) {
        return new Comparator.FieldGt(field, value);
    }

    static Comparator geq(String field, Object value) {
        return new Comparator.FieldGeq(field, value);
    }

    static Comparator lt(String field, Object value) {
        return new Comparator.FieldLt(field, value);
    }

    static Comparator leq(String field, Object value) {
        return new Comparator.FieldLeq(field, value);
    }

    @Test
    void testFieldEqPredicate() {
        Fact fact = new Fact(MapTerm.of("name", "Douglas", "age", 42));
        Predicate namePredicate = eq("name", "Douglas");
        Predicate agePredicate = eq("age", 42);

        assertTrue(namePredicate.test(fact));
        assertTrue(agePredicate.test(fact));

        Predicate wrongNamePredicate = eq("name", "John");
        Predicate wrongAgePredicate = eq("age", 25);

        assertFalse(wrongNamePredicate.test(fact));
        assertFalse(wrongAgePredicate.test(fact));
    }

    @Test
    void testAndPredicate() {
        Fact fact = new Fact(MapTerm.of("name", "Douglas", "age", 42));
        Predicate namePredicate = eq("name", "Douglas");
        Predicate agePredicate = eq("age", 42);

        Predicate<Fact, FactSet> andPredicate = namePredicate.and(agePredicate);

        assertTrue(andPredicate.test(fact));

        Predicate wrongAgePredicate = eq("age", 25);
        Predicate<Fact, FactSet> andPredicateFalse = namePredicate.and(wrongAgePredicate);

        assertFalse(andPredicateFalse.test(fact));
    }

    @Test
    void testAndListPredicate() {
        Fact fact = new Fact(MapTerm.of("name", "Douglas", "age", 42));
        Predicate namePredicate = eq("name", "Douglas");
        Predicate agePredicate = eq("age", 42);

        Predicate<Fact, FactSet> allPredicate = Predicates.and(namePredicate, agePredicate);

        assertTrue(allPredicate.test(fact));

        Predicate wrongAgePredicate = eq("age", 25);
        Predicate<Fact, FactSet> PredicateFalse = Predicates.and((Predicate) allPredicate, wrongAgePredicate);

        assertFalse(PredicateFalse.test(fact));
    }

    @Test
    void testOrListPredicate() {
        Fact fact = new Fact(MapTerm.of("name", "Douglas", "age", 42));
        Predicate namePredicate = eq("name", "Douglas");
        Predicate agePredicate = eq("age", 43);

        Predicate<Fact, FactSet> allPredicate = Predicates.or(namePredicate, agePredicate);

        assertTrue(allPredicate.test(fact));

        Predicate wrongAgePredicate = eq("age", 25);
        Predicate<Fact, FactSet> PredicateFalse = Predicates.or(agePredicate, wrongAgePredicate);

        assertFalse(PredicateFalse.test(fact));
    }

    @Test
    void testOrListPredicate2() {
        Fact fact = new Fact(MapTerm.of("name", "Douglas", "age", 42));
        Predicate namePredicate = eq("name", "Douglas");
        Predicate wrongNamePredicate = eq("name", "John");

        Predicate<Fact, FactSet> orPredicate = namePredicate.or(wrongNamePredicate);

        assertTrue(orPredicate.test(fact));

        Predicate wrongAgePredicate = eq("age", 25);
        Predicate<Fact, FactSet> orBothWrongPredicate = wrongNamePredicate.or(wrongAgePredicate);

        assertFalse(orBothWrongPredicate.test(fact));
    }

    @Test
    void testNotPredicate() {
        Fact fact = new Fact(MapTerm.of("name", "Douglas", "age", 42));
        Predicate namePredicate = eq("name", "Douglas");

        Predicate<Fact, FactSet> notPredicate = namePredicate.not();

        assertFalse(notPredicate.test(fact));

        Predicate wrongNamePredicate = eq("name", "John");
        Predicate<Fact, FactSet> notWrongNamePredicate = wrongNamePredicate.not();

        assertTrue(notWrongNamePredicate.test(fact));
    }

    @Test
    void testComposedPredicates() {
        Fact fact = new Fact(MapTerm.of("name", "Douglas", "age", 42));
        Predicate namePredicate = eq("name", "Douglas");
        Predicate agePredicate = eq("age", 42);
        Predicate wrongNamePredicate = eq("name", "John");

        Predicate<Fact, FactSet> andPredicate = namePredicate.and(agePredicate);
        Predicate<Fact, FactSet> orPredicate = namePredicate.or(wrongNamePredicate);
        Predicate<Fact, FactSet> notPredicate = namePredicate.not();

        Predicate<Fact, FactSet> complexPredicate = andPredicate.or(notPredicate);

        assertTrue(complexPredicate.test(fact));

        Predicate<Fact, FactSet> complexNegatedPredicate = notPredicate.and(wrongNamePredicate);

        assertFalse(complexNegatedPredicate.test(fact));
    }


    @Test
    void testFieldGeqPredicate() {
        Fact fact = new Fact(MapTerm.of("name", "Douglas", "age", 42));
        Predicate agePredicate = geq("age", 40);
        assertTrue(agePredicate.test(fact));
        assertTrue(geq("age", 42).test(fact));

        Predicate wrongAgePredicate = geq("age", 43);
        assertFalse(wrongAgePredicate.test(fact));
    }

    @Test
    void testFieldLtPredicate() {
        Fact fact = new Fact(MapTerm.of("name", "Douglas", "age", 42));
        assertTrue(lt("age", 45).test(fact));
        assertTrue(lt("age", 45.0).test(fact));
        assertFalse(lt("age", 42).test(fact));
        assertFalse(lt("age", 42.0).test(fact));

        Predicate wrongAgePredicate = lt("age", 41);
        assertFalse(wrongAgePredicate.test(fact));
    }

    @Test
    void testFieldGtPredicate() {
        Fact fact = new Fact(MapTerm.of("name", "Douglas", "age", 42));
        assertTrue(gt("age", 40).test(fact));
        assertTrue(gt("age", 40.0).test(fact));
        assertFalse(gt("age", 42).test(fact));
        assertFalse(gt("age", 42.0).test(fact)); // double

        Predicate wrongAgePredicate = gt("age", 43);
        assertFalse(wrongAgePredicate.test(fact));
    }

    @Test
    void testFieldLeqPredicate() {
        Fact fact = new Fact(MapTerm.of("name", "Douglas", "age", 42));
        assertTrue(leq("age", 45).test(fact));
        assertTrue(leq("age", 45.0).test(fact));
        assertTrue(leq("age", 42).test(fact));
        assertTrue(leq("age", 42.0).test(fact));

        Predicate wrongAgePredicate = leq("age", 41);
        assertFalse(wrongAgePredicate.test(fact));
    }

    @Test
    void testFieldListContainsPredicate() {
        Fact fact = new Fact(MapTerm.of("tags", List.of("java", "openai", "ai")));
        FieldContains tagsPredicate = new FieldContains("tags", "java");
        assertTrue(tagsPredicate.test(fact));

        FieldContains wrongTagPredicate = new FieldContains("tags", "python");
        assertFalse(wrongTagPredicate.test(fact));
    }

    @Test
    void testFieldStringContainsPredicate() {
        Fact fact = new Fact(MapTerm.of("name", "foo bar"));
        FieldContains tagsPredicate = new FieldContains("name", "bar");
        assertTrue(tagsPredicate.test(fact));

        FieldContains wrongTagPredicate = new FieldContains("name", "baz");
        assertFalse(wrongTagPredicate.test(fact));
    }

    @Test
    void testFieldListInPredicate() {
        Fact fact = new Fact(MapTerm.of("status", "active"));
        FieldIn statusPredicate = new FieldIn("status", List.of("active", "inactive"));
        assertTrue(statusPredicate.test(fact));

        FieldIn wrongStatusPredicate = new FieldIn("status", List.of("pending", "deleted"));
        assertFalse(wrongStatusPredicate.test(fact));
    }

    @Test
    void testFieldStringInPredicate() {
        Fact fact = new Fact(MapTerm.of("name", "bar"));
        FieldIn statusPredicate = new FieldIn("name", "foo bar");
        assertTrue(statusPredicate.test(fact));

        FieldIn wrongStatusPredicate = new FieldIn("name", "foo");
        assertFalse(wrongStatusPredicate.test(fact));
    }

    @Test
    void testFieldGtDynamicBindPredicate() {

        FactSet params = FactSets.create("params", MapTerm.of("foo", "bar", "threshold", 30));

        Fact fact = new Fact(MapTerm.of("name", "Douglas", "age", 42));

        Comparator cgt = gt("age", from("params").then(select("threshold")));
        assertThrows(MaxRegelException.class, () -> cgt.test(fact)); // gt is not bound yet

        Predicate concreteCmp = cgt.bind(params);// now it is bound
        assertTrue(concreteCmp.test(fact)); // 42 IS greater 30

        params = FactSets.create("params", MapTerm.of("foo", "bar", "threshold", 50));
        concreteCmp = cgt.bind(params);// now it is bound
        assertFalse(concreteCmp.test(fact)); // 42 IS NOT greater 30
    }

    @Test
    void testAnyPredicate() {
        FactSet facts = FactSets.create(
                MapTerm.of("name", "Douglas", "age", 42),
                MapTerm.of("name", "Mary", "age", 45)
        );

        assertTrue(new Any(gt("age", 43)).test(facts));
        assertFalse(new Any(gt("age", 50)).test(facts));
    }

    @Test
    void testAllPredicate() {
        FactSet facts = FactSets.create(
                MapTerm.of("name", "Douglas", "age", 42),
                MapTerm.of("name", "Mary", "age", 45)
        );

        assertTrue(new All(gt("age", 40)).test(facts));
        assertFalse(new All(gt("age", 43)).test(facts));
    }
}