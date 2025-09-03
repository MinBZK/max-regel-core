package io.github.zvasva.maxregel.core.process;

import io.github.zvasva.maxregel.core.factset.FactSet;
import io.github.zvasva.maxregel.core.factset.FactSets;
import io.github.zvasva.maxregel.core.factset.SinglePartFactSet;
import io.github.zvasva.maxregel.core.process.factoperation.SetPart;
import io.github.zvasva.maxregel.core.process.predicate.Comparator.FieldEq;
import io.github.zvasva.maxregel.core.process.predicate.Comparator.FieldGeq;
import io.github.zvasva.maxregel.core.process.predicate.Comparator.FieldGt;
import io.github.zvasva.maxregel.core.process.predicate.Comparator.FieldLt;
import io.github.zvasva.maxregel.core.process.predicate.Exists;
import io.github.zvasva.maxregel.core.process.rule.*;
import io.github.zvasva.maxregel.core.term.Fact;
import io.github.zvasva.maxregel.core.term.MapTerm;
import io.github.zvasva.maxregel.core.term.Terms;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static io.github.zvasva.maxregel.core.factset.Empty.EMPTY;
import static io.github.zvasva.maxregel.core.factset.FactSetTest.simpsons;
import static io.github.zvasva.maxregel.core.process.rule.Rules.*;
import static io.github.zvasva.maxregel.util.Iters.first;
import static io.github.zvasva.maxregel.util.PrettyPrint.print;
import static org.junit.jupiter.api.Assertions.*;

public class RuleTest {


    @Test
    public void testFrom() {
        FactSet a = FactSets.create("a", MapTerm.of("x", 1));
        FactSet b = FactSets.create("b", MapTerm.of("y", 2));
        FactSet facts = a.union(b);

        assertEquals(a, new From("a").apply(facts));
        assertEquals(b, new From("b").apply(facts));
        assertEquals(EMPTY, new From("c").apply(facts));
    }


    @Test
    public void testSetPart() {
        FactSet a = FactSets.create("a", MapTerm.of("x", 1));
        FactSet b = FactSets.create("b", MapTerm.of("y", 2));
        FactSet facts = a.union(b); // two parts

        FactSet output = new SetPart("c").apply(facts);
        assertEquals(Set.of("c"), output.parts());
        assertEquals(2, output.size());
    }

    @Test
    public void testRemove() {
        FactSet a = FactSets.create("a", MapTerm.of("x", 1));
        FactSet b = FactSets.create("b", MapTerm.of("y", 2));
        FactSet facts = a.union(b);

        assertEquals(b, new Remove("a").apply(facts));
        assertEquals(a, new Remove("b").apply(facts));
        assertEquals(facts, new Remove("c").apply(facts));
    }

    @Test
    public void testAssignSet() {
        FactSet input = FactSets.create("input", MapTerm.of("x", 1));
        print("input", input);
        Rule assignset = new AssignSet("output", new Arithmetic.Add("y", new From("input"), new From("input")));
        FactSet output = assignset.apply(input);
        print("output", output);

        FactSet expected = input.union(FactSets.create("output", MapTerm.of("y", 2.0)));
        print("expected", expected);
        assertEquals(expected, output);

        // Now use a tracer
        RuleResult ruleResult = assignset.apply(input, Tracer.ASSIGNMENTS);
        print("total", ruleResult.total());
        assertEquals(expected, ruleResult.total());

        print("update", ruleResult.update());
        assertEquals(ruleResult.update(), ruleResult.total().remove("input"));
    }


    @Test
    public void testAssignSetStar() {
        FactSet input = FactSets.create("input", MapTerm.of("x", 1));
        print("\nActual input", input);
        Rule assignset = new AssignSet("*", new Arithmetic.Add("y", new From("input"), new From("input")).then(new SetPart("output")));
        FactSet output = assignset.apply(input);
        print("\nActual output", output);

        FactSet expected = input.union(FactSets.create("output", MapTerm.of("y", 2.0)));
        print("\nActual expected", expected);
        assertEquals(expected, output);
        assertTrue(output.has("input"));
        assertTrue(output.has("output"));

        // Now use a tracer
        RuleResult ruleResult = assignset.apply(input, Tracer.ASSIGNMENTS);
        print("\nActual total", ruleResult.total());
        assertTrue(ruleResult.total().has("input"));
        assertTrue(ruleResult.total().has("output"));
        assertEquals(expected, ruleResult.total());

        print("\nActual update", ruleResult.update());
        assertFalse(ruleResult.update().has("input"));
        assertTrue(ruleResult.update().has("output"));
        assertEquals(ruleResult.update(), ruleResult.total().remove("input"));
    }



    @Test
    public void testAssignUpdate() {
        FactSet input = FactSets.create("input", MapTerm.of("x", 1));
        print("input", input);

        Rule assignUpdate = new AssignUpdate("output", new Arithmetic.Add("y", new From("input"), new From("input")));

        FactSet output = assignUpdate.apply(input);
        print("output", output);

        FactSet expected = input.union(FactSets.create("output", MapTerm.of("y", 2.0)));
        print("expected", expected);
        assertEquals(expected, output);

        // Now use a tracer
        RuleResult ruleResult = assignUpdate.apply(input, Tracer.ASSIGNMENTS);
        print("total", ruleResult.total());
        assertEquals(expected, ruleResult.total());

        print("update", ruleResult.update());
        assertEquals(ruleResult.update(), ruleResult.total().remove("input"));
    }


    @Test
    public void testSortByStringField() {
        FactSet facts = FactSets.create(
            MapTerm.of("name", "Charlie"),
            MapTerm.of("name", "Alice"),
            MapTerm.of("name", "Bob")
        );
        FactSet sorted = new Sort("name").apply(facts);
        assertEquals("Alice", first(sorted).get("name"));
        List<String> names = sorted.stream().map(f -> (String) f.get("name")).toList();
        assertEquals(List.of("Alice", "Bob", "Charlie"), names);

        FactSet sortedDesc = new Sort("name", true).apply(facts);
        List<String> namesDesc = sortedDesc.stream().map(f -> (String) f.get("name")).toList();
        assertEquals(List.of("Charlie", "Bob", "Alice"), namesDesc);
    }

    @Test
    public void testSortByNumericField() {
        FactSet facts = FactSets.create(
            MapTerm.of("amount", 42),
            MapTerm.of("amount", 7),
            MapTerm.of("amount", 15)
        );
        FactSet sorted = new Sort("amount").apply(facts);
        List<Integer> vals = sorted.stream().map(f -> (Integer) f.get("amount")).toList();
        assertEquals(List.of(7, 15, 42), vals);

        FactSet sortedDesc = new Sort("amount", true).apply(facts);
        List<Integer> valsDesc = sortedDesc.stream().map(f -> (Integer) f.get("amount")).toList();
        assertEquals(List.of(42, 15, 7), valsDesc);
    }

    @Test
    public void testSortByDateField() {
        FactSet facts = FactSets.create(
            MapTerm.of("date", java.time.LocalDate.of(2023, 1, 1)),
            MapTerm.of("date", java.time.LocalDate.of(2021, 6, 15)),
            MapTerm.of("date", java.time.LocalDate.of(2022, 12, 31))
        );
        FactSet sorted = new Sort("date").apply(facts);
        List<java.time.LocalDate> dates = sorted.stream().map(f -> (java.time.LocalDate) f.get("date")).toList();
        assertEquals(List.of(
            java.time.LocalDate.of(2021, 6, 15),
            java.time.LocalDate.of(2022, 12, 31),
            java.time.LocalDate.of(2023, 1, 1)
        ), dates);

        FactSet sortedDesc = new Sort("date", true).apply(facts);
        List<java.time.LocalDate> datesDesc = sortedDesc.stream().map(f -> (java.time.LocalDate) f.get("date")).toList();
        assertEquals(List.of(
            java.time.LocalDate.of(2023, 1, 1),
            java.time.LocalDate.of(2022, 12, 31),
            java.time.LocalDate.of(2021, 6, 15)
        ), datesDesc);
    }

    @Test
    public void testAllEqualAggregate() {
        // All equal
        FactSet allSame = FactSets.create(
            MapTerm.of("x", 42),
            MapTerm.of("x", 42),
            MapTerm.of("x", 42)
        );
        FactSet result = new AllEqual("x").apply(allSame);
        assertEquals(true, first(result).get("allEqual"));

        // Not all equal
        FactSet notAllSame = FactSets.create(
            MapTerm.of("x", 42),
            MapTerm.of("x", 41),
            MapTerm.of("x", 42)
        );
        FactSet result2 = new AllEqual("x").apply(notAllSame);
        assertEquals(false, first(result2).get("allEqual"));

        // Single value
        FactSet single = FactSets.create(MapTerm.of("x", 99));
        FactSet result3 = new AllEqual("x").apply(single);
        assertEquals(true, first(result3).get("allEqual"));

        // Empty set
        FactSet empty = EMPTY;
        FactSet result4 = new AllEqual("x").apply(empty);
        assertEquals(true, first(result4).get("allEqual"));

        // Null values
        FactSet nulls = FactSets.create(
            MapTerm.of("x", null),
            MapTerm.of("x", null)
        );
        FactSet result5 = new AllEqual("x").apply(nulls);
        assertEquals(true, first(result5).get("allEqual"));

        // Mixed null and value
        FactSet mixed = FactSets.create(
            MapTerm.of("x", null),
            MapTerm.of("x", 1)
        );
        FactSet result6 = new AllEqual("x").apply(mixed);
        assertEquals(false, first(result6).get("allEqual"));
    }

    @Test
    public void testSortByLocalDateTimeField() {
        FactSet facts = FactSets.create(
            MapTerm.of("dt", java.time.LocalDateTime.of(2023, 1, 1, 10, 0)),
            MapTerm.of("dt", java.time.LocalDateTime.of(2021, 6, 15, 12, 30)),
            MapTerm.of("dt", java.time.LocalDateTime.of(2022, 12, 31, 9, 45))
        );
        FactSet sorted = new Sort("dt").apply(facts);
        List<java.time.LocalDateTime> dts = sorted.stream().map(f -> (java.time.LocalDateTime) f.get("dt")).toList();
        assertEquals(List.of(
            java.time.LocalDateTime.of(2021, 6, 15, 12, 30),
            java.time.LocalDateTime.of(2022, 12, 31, 9, 45),
            java.time.LocalDateTime.of(2023, 1, 1, 10, 0)
        ), dts);

        FactSet sortedDesc = new Sort("dt", true).apply(facts);
        List<java.time.LocalDateTime> dtsDesc = sortedDesc.stream().map(f -> (java.time.LocalDateTime) f.get("dt")).toList();
        assertEquals(List.of(
            java.time.LocalDateTime.of(2023, 1, 1, 10, 0),
            java.time.LocalDateTime.of(2022, 12, 31, 9, 45),
            java.time.LocalDateTime.of(2021, 6, 15, 12, 30)
        ), dtsDesc);
    }

    static final Rule hasChildren = new BasicRule(
            factset -> FactSets.create(MapTerm.of("hasChilderen",
                                                      factset.any(new FieldLt("age", 18))))
    );

    static final Rule hasMen = new BasicRule(
            factset -> FactSets.create(MapTerm.of("hasMen",
                    factset.any(new FieldEq("gender", "male"))))
    );

    static final Rule hasOldMen = new BasicRule(
            factset -> FactSets.create(MapTerm.of("hasOldMen",
                    factset.any(new FieldEq("gender", "male").and(new FieldGt("age", 40)))))
    );

    static final Rule get_size = new BasicRule(
        factset -> {
        long size = factset.size();
        return FactSets.create("set_size", MapTerm.of("size", size));
        }
    );

    @Test
    public void testSize(){
        FactSet test_set = new SinglePartFactSet(List.of(
            new Fact(MapTerm.of("bla", "bloe")),
            new Fact(MapTerm.of("bla", "blie"))
            ));
        FactSet newFacts = get_size.apply(test_set);
        Fact fun_fact = first(newFacts);
        System.out.println(fun_fact);
        assertTrue(newFacts.get("set_size").any(new FieldEq("size", (long) 2)));
    }

    @Test
    public void testHasChildren(){
        FactSet newFacts = hasChildren.apply(simpsons);
        assertTrue(newFacts.any(new FieldEq("hasChilderen", true)));
    }

    @Test
    public void testHasMen(){
        FactSet newFacts = hasMen.apply(simpsons);
        assertTrue(newFacts.any(new FieldEq("hasMen", true)));
    }

    @Test
    public void testHasOldMen(){
        FactSet newFacts = hasOldMen.apply(simpsons);
        assertTrue(newFacts.any(new FieldEq("hasOldMen", true)));
    }

    @Test
    public void testHasUndefinedField(){
        FactSet newFacts = hasOldMen.apply(simpsons);
        assertFalse(newFacts.any(new FieldEq("foo", true)));
    }

    @Test
    public void testOlderThanHomer(){
        Rule rule = filter(new FieldGt("age", filter("name", "==", "Homer").then(select("age"))));

        print(rule);
        FactSet olderThanHomer = rule.apply(simpsons);
        print(olderThanHomer);
        assertEquals(4, olderThanHomer.size());
    }


    @Test
    public void testNotOlderThanHomer(){
        Rule rule = filter(new FieldGt("age", filter("name", "==", "Homer").then(select("age"))).not());

        print(rule);
        FactSet notOlderThanHomer = rule.apply(simpsons);
        print(notOlderThanHomer);
        assertEquals(5, notOlderThanHomer.size());
    }


    @Test
    public void testHasSantaLike(){

        List<Rule> rules = List.of(
                new AssignSet("santa_candidates",  new From("simpsons").then(new Filter(new FieldEq("name", "Santa")))),
                new AssignSet("santa_candidates", new From("old_males").then(new Filter(new FieldEq("hair", "middle").and(new FieldGt("weight", 150))))),
                new AssignSet("old_males", new From("simpsons").then(new Filter(new FieldEq("gender", "male").and(new FieldGt("age", 40)))))
        );


        FactSet newFacts = Inference.infer2(simpsons, rules, new Tracer.Assignments(), 10);
        FactSet santas = newFacts.get("santa_candidates");
        System.out.println("newFacts = " + newFacts);
        System.out.println("santas = " + santas);
        assertFalse(santas.isEmpty());

        System.out.println("rules = " + rules);
    }

    @Test
    public void testCaseOnAge(){
        print(simpsons);

        Case kees = new Case(List.of(
                new Case.LookupEntry(new FieldLt("age", 19), cnst("minor")),
                new Case.LookupEntry(new FieldLt("age", 68), cnst("adult")),
                new Case.LookupEntry(new FieldGeq("age", 68), cnst("adult (AOW)"))
        ), cnst("?"), "x");

        FactSet result = kees.apply(simpsons);
        print(result);
        assertEquals(3, result.filter(new FieldEq("x", "minor")).size());
        assertEquals(1, result.filter(new FieldEq("x", "adult (AOW)")).size());
    }

    @Test
    public void testFactSetCaseOnAge(){
        print(simpsons);

        List<FactSetCase.LookupEntry> lookup = List.of(
                new FactSetCase.LookupEntry(from("simpsons").then(filter("age", ">", 100)), new Exists(), cnst("includes dinosaurs") ),
                new FactSetCase.LookupEntry(from("simpsons").then(filter("age", ">", 60)), new Exists(), cnst("includes elderly") )
        );

        FactSetCase kees = new FactSetCase(lookup, cnst("somewhat you population"));

        FactSet result = kees.apply(simpsons);
        print(result);
        assertEquals("includes elderly", FactSets.value(result));
    }


    @Test
    public void testMerge(){
        FactSet A = FactSets.create("first",
                MapTerm.of("A1", 1),
                MapTerm.of("B1", 2),
                MapTerm.of("C1", 3)
                );

        FactSet B = FactSets.create( "second",
                MapTerm.of("A2", 4), // add field
                MapTerm.of("B1", 5) // overwrite
                );

        FactSet facts = A.union(B);
        FactSet merged = new Merge(from("first"), from("second")).apply(facts);
        print(merged);

        FactSet expected = FactSets.create(
                MapTerm.of("A1", 1, "A2", 4),
                MapTerm.of("B1", 5)
        );

        assertEquals(expected, merged);
    }



    @Test
    public void testSubTwoFields(){
        FactSet A = FactSets.create("revenue",
                MapTerm.of("r", 5, "project", "A"),
                MapTerm.of("r", 4, "project", "B"),
                MapTerm.of("r", 3, "project", "C")
        );

        FactSet B = FactSets.create( "expense",
                MapTerm.of("e", 1),
                MapTerm.of("e", 2)
        );

        FactSet facts = A.union(B);
        print(facts);
        FactSet merged = new Arithmetic.Sub("x", from("revenue"), from("expense")).apply(facts);
        print(merged);

        FactSet expected = FactSets.create(
                MapTerm.of("x", 4.0),
                MapTerm.of("x", 2.0)
        );

        assertEquals(expected, merged);
    }

    @Test
    public void testAddTwoFieldsStrange(){
        FactSet A = FactSets.create("first",
                MapTerm.of("A1", 1),
                MapTerm.of("B1", 2),
                MapTerm.of("C1", 3)
                );

        FactSet B = FactSets.create( "second",
                MapTerm.of("A2", 4), // add field
                MapTerm.of("B1", 5) // overwrite
                );

        FactSet facts = A.union(B);
        print(facts);
        FactSet merged = new Arithmetic.Add("x", from("first"), from("second")).apply(facts);
        print(merged);

        FactSet expected = FactSets.create(
                MapTerm.of("x", 5.0),
                MapTerm.of("x", 7.0)
        );

        assertEquals(expected, merged);
    }

    @Test
    public void testAddFieldAndConstant(){
        FactSet A = FactSets.create("first",
                MapTerm.of("A1", 1),
                MapTerm.of("B1", 2),
                MapTerm.of("C1", 3)
        );

        FactSet sum1 = new Arithmetic.Add("x", from("first"), cnst(4)).apply(A);
        // swapped args
        FactSet sum2 = new Arithmetic.Add("x", cnst(4), from("first")).apply(A);

        print(sum1);

        FactSet expected = FactSets.create(
                MapTerm.of("x", 5.0)
        );

        assertEquals(expected, sum1);
        assertEquals(expected, sum2);
    }

    @Test
    public void testCompareTwoFields(){
        FactSet A = FactSets.create("A",
                MapTerm.of("a", 6),
                MapTerm.of("a", 2),
                MapTerm.of("a", 3)
        );

        FactSet B = FactSets.create( "B",
                MapTerm.of("b", 4),
                MapTerm.of("b", 5)
        );

        FactSet facts = A.union(B);

        FactSet result = new Compare("x", new FieldGt(), from("A"), from("B")).apply(facts);

        FactSet expected = FactSets.create(
                MapTerm.of("x", true), // 6 > 4
                MapTerm.of("x", false) // 2 > 5
        );

        assertEquals(expected, result);
    }



    @Test
    public void testSelect(){
        FactSet A = FactSets.create("A",
                MapTerm.of("A", 1, "B", 42),
                MapTerm.of("A", 2, "B", 43),
                MapTerm.of("A", 3, "B", 44)
        );

        FactSet columnB = new SelectFields(List.of("B")).apply(A);
        print(columnB);

        FactSet expected = FactSets.create(
                MapTerm.of("B", 42),
                MapTerm.of("B", 43),
                MapTerm.of("B", 44)
        );

        assertEquals(expected, columnB);
    }

    @Test
    public void testSum1(){
        FactSet facts = FactSets.create(
                MapTerm.of("A", 2),
                MapTerm.of("A", 1),
                MapTerm.of("A", 3)
        );
        FactSet sum = new Aggregate.Sum().apply(facts);
        assertEquals(6.0, Terms.first(first(sum).getTerm()));
    }

    @Test
    public void testSumMissing(){
        FactSet facts = FactSets.create(
                MapTerm.of("A", null),
                MapTerm.of("A", 1),
                MapTerm.of("A", 3)
        );
        FactSet sum = new Aggregate.Sum().apply(facts);
        assertEquals(4.0, Terms.first(first(sum).getTerm()));
    }

    @Test
    public void testSumMissingAll(){
        FactSet facts = FactSets.create(
                Stream.of()
        );
        FactSet sum = new Aggregate.Sum().apply(facts);
        assertNull(Terms.first(first(sum).getTerm()));
    }

    @Test
    public void testMin(){
        FactSet facts = FactSets.create(
                MapTerm.of("A", null),
                MapTerm.of("A", 1),
                MapTerm.of("A", 3)
        );
        FactSet y = new Aggregate.Min().apply(facts);
        assertEquals(1, Terms.first(first(y).getTerm()));
    }

    @Test
    public void testMax(){
        FactSet facts = FactSets.create(
                MapTerm.of("A", null),
                MapTerm.of("A", 1),
                MapTerm.of("A", 3)
        );
        FactSet y = new Aggregate.Max().apply(facts);
        assertEquals(3, Terms.first(first(y).getTerm()));
    }

    @Test
    public void testCountEmpty(){
        FactSet facts = FactSets.create(
                Stream.of()
        );
        FactSet y = new Count().apply(facts);
        assertEquals(0L, Terms.first(first(y).getTerm()));
    }

    @Test
    public void testCount(){
        FactSet facts = FactSets.create(
                MapTerm.of("A", null),
                MapTerm.of("A", 1),
                MapTerm.of("A", 3)
        );
        FactSet y = new Count().apply(facts);
        assertEquals(3L, Terms.first(first(y).getTerm()));
    }

    @Test
    public void testAggregateByCount(){

        FactSet y = new AggregateBy("gender", "name", new Aggregate.Count()).apply(simpsons);
        print(y);
        assertEquals(2, y.size());
    }


    @Test
    public void testAggregateByMaxAge(){

        FactSet y = new AggregateBy("gender", "age", new Aggregate.Max()).apply(simpsons);
        print(y);
        assertEquals(List.of(70, 41), FactSets.getField(y, "aggregate_max_age"));
    }

    @Test
    public void testFlatMap(){

        FactSet fm = new FlatMap(
                new Arithmetic.Div("weightPerAge", new SelectFields("weight"), new SelectFields("age")))
                .apply(simpsons);

        FactSet direct = new Arithmetic.Div("weightPerAge", new SelectFields("weight"), new SelectFields("age"))
                .apply(simpsons);

        assertEquals(fm, direct);
    }

    @Test
    public void testArchiver(){
        FactSet facts = FactSets.create(
                MapTerm.of("A", "menno", "B", "arvid"),
                MapTerm.of("B", 1),
                MapTerm.of("C", 3)
        );
        print("original", facts);
        FactSet archive = new Archiver().apply(facts).get("archive");
        print("archive", archive);
        assertEquals(4L, archive.size());

        assertEquals("menno", first(archive).get("value"));
    }

    @Test
    public void testIdentity() {
        FactSet simpsons2 = new Identity().apply(simpsons);
        assertEquals(simpsons, simpsons2);
    }

    @Test
    public void testReturnIf1() {
        FactSet facts = FactSets.create(
                FactSets.create("FA", MapTerm.of("A", "menno", "B", "arvid")),
                FactSets.create("FB", MapTerm.of("B", 1)),
                FactSets.create("FC", MapTerm.of("C", 3))
        );
        ReturnIf returnIf = new ReturnIf(new From("FB"));
        assertTrue(returnIf.condition(facts));
    }

    @Test
    public void testReturnIf2() {
        FactSet facts = FactSets.create(
                FactSets.create("FA", MapTerm.of("A", "menno", "B", "arvid")),
                FactSets.create("FB", MapTerm.of("B", 1)),
                FactSets.create("FC",MapTerm.of("C", 3))
        );
        ReturnIf returnIf = new ReturnIf(new From("FB"), new Exists(), new From("FC"));
        assertTrue(returnIf.condition(facts));
        assertEquals(3, FactSets.value(returnIf.result(facts)));

    }


    @Test
    public void testScriptReturnIf2() {
        FactSet facts = FactSets.create(
                FactSets.create("FA", MapTerm.of("A", 1)),
                FactSets.create("FB", MapTerm.of("B", 2)),
                FactSets.create("FC",MapTerm.of("C", 3))
        );
        Script script = script(
                let("FAA", from("FA")),
                new ReturnIf(from("FAA")),
                let("FCC", from("FC"))
        );

        FactSet result = script.apply(facts);
        print(result);
        assertTrue(result.has("FAA"));
        assertFalse(result.has("FCC"));
    }

    @Test
    public void testSubReturnIf1() {
        FactSet facts = FactSets.create(
                FactSets.create("FA", MapTerm.of("A", 1)),
                FactSets.create("FB", MapTerm.of("B", 2)),
                FactSets.create("FC",MapTerm.of("C", 3))
        );
        Script script = script(
//                new Print()
                let("FAA", from("FA")),
                let("FBB", script(
                        let("SUBFB_SHOULD_BE_RETURNED", from("FB")),
                        new ReturnIf(from("SUBFB_SHOULD_BE_RETURNED"),
                                new Exists(), // condition
                                from("SUBFB_SHOULD_BE_RETURNED") // result
                        ),
                        let("SUBFA_SHOULD_NOT_BE_RETURNED", from("FA")),
                        from("SUBFA_SHOULD_NOT_BE_RETURNED")
                )),
                let("FCC", from("FC"))
        );

        print(script);
        FactSet result = script.apply(facts, new Tracer.Assignments()).update();
        print(result);
        assertTrue(result.has("FAA"));
        assertTrue(result.has("FBB"));
        assertEquals(2, FactSets.value(result.get("FBB"), "B"));
        assertTrue(result.has("FCC"));
    }
}
