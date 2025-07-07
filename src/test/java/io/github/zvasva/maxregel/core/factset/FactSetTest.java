package io.github.zvasva.maxregel.core.factset;


import io.github.zvasva.maxregel.core.process.UnaryOperation;
import io.github.zvasva.maxregel.core.process.factoperation.ComputeAge;
import io.github.zvasva.maxregel.core.process.predicate.Comparator;
import io.github.zvasva.maxregel.core.process.predicate.Comparator.FieldEq;
import io.github.zvasva.maxregel.core.process.predicate.Comparator.FieldGt;
import io.github.zvasva.maxregel.core.process.predicate.Comparator.FieldLt;
import io.github.zvasva.maxregel.core.process.predicate.Predicate;
import io.github.zvasva.maxregel.core.term.Fact;
import io.github.zvasva.maxregel.core.term.MapTerm;
import io.github.zvasva.maxregel.core.term.Term;
import io.github.zvasva.maxregel.db.JdbcFactSet;
import io.github.zvasva.maxregel.util.Iters;
import io.github.zvasva.maxregel.util.PrettyPrint;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static io.github.zvasva.maxregel.core.factset.FactSets.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * Test class for the {@link FactSet} interface.
 * <p>
 * This class performs setup operations and various test cases to ensure the
 * functionalities of the FactSet implementation are working as expected.
 */
public class FactSetTest {

    public static final FactSet simpsons = FactSets.create("simpsons",
        MapTerm.of("name", "Homer", "hair", "short", "weight", 250, "age", 36, "gender", "male"),
        MapTerm.of("name", "Marge", "hair", "long", "weight", 150, "age", 35, "gender", "female"),
        MapTerm.of("name", "Bart", "hair", "short", "weight", 90, "age", 10, "gender", "male"),
        MapTerm.of("name", "Lisa", "hair", "middle", "weight", 78, "age", 8, "gender", "female"),
        MapTerm.of("name", "Maggie", "hair", "middle", "weight", 20, "age", 1, "gender", "female"),
        MapTerm.of("name", "Abe", "hair", "short", "weight", 170, "age", 70, "gender", "male"),
        MapTerm.of("name", "Selma", "hair", "long", "weight", 160, "age", 41, "gender", "female"),
        MapTerm.of("name", "Otto", "hair", "long", "weight", 180, "age", 38, "gender", "male"),
        MapTerm.of("name", "Krusty", "hair", "middle", "weight", 200, "age", 45, "gender", "male")
    );

    public static final FactSet simpsonsPart1 = FactSets.create("simpsonsPart1",
        MapTerm.of("name", "Homer", "hair", "short", "weight", 250, "age", 36, "gender", "male"),
        MapTerm.of("name", "Marge", "hair", "long", "weight", 150, "age", 35, "gender", "female"),
        MapTerm.of("name", "Bart", "hair", "short", "weight", 90, "age", 10, "gender", "male"),
        MapTerm.of("name", "Lisa", "hair", "middle", "weight", 78, "age", 8, "gender", "female"),
        MapTerm.of("name", "Maggie", "hair", "middle", "weight", 20, "age", 1, "gender", "female")
    );

    public static final FactSet simpsonsPart2 = FactSets.create("simpsonsPart2",
        MapTerm.of("name", "Abe", "hair", "short", "weight", 170, "age", 70, "gender", "male"),
        MapTerm.of("name", "Selma", "hair", "long", "weight", 160, "age", 41, "gender", "female"),
        MapTerm.of("name", "Otto", "hair", "long", "weight", 180, "age", 38, "gender", "male"),
        MapTerm.of("name", "Krusty", "hair", "middle", "weight", 200, "age", 45, "gender", "male")
    );


    public static final FactSet concatSimpsons = new Concat(simpsonsPart1, simpsonsPart2);

    static Connection connection;
    public static FactSet dbSimpsons;

    public static final FactSet duplicateSimpsons = FactSets.create("duplicates",
        MapTerm.of("name", "Homer", "hair", "short", "weight", 250, "age", 36, "gender", "male"),
        MapTerm.of("name", "Marge", "hair", "long", "weight", 150, "age", 35, "gender", "female"),
        MapTerm.of("name", "Marge", "hair", "long", "weight", 150, "age", 35, "gender", "female"),
        MapTerm.of("name", "Homer", "hair", "short", "weight", 250, "age", 36, "gender", "male")
    );

    public static final FactSet residents = FactSets.create("residents",
        MapTerm.of("name", "Homer", "address", "742 Evergreen Terrace"),
        MapTerm.of("name", "Marge", "address", "742 Evergreen Terrace"),
        MapTerm.of("name", "Bart", "address", "742 Evergreen Terrace"),
        MapTerm.of("name", "Lisa", "address", "742 Evergreen Terrace"),
        MapTerm.of("name", "Maggie", "address", "742 Evergreen Terrace"),
        MapTerm.of("name", "Abe", "address", "Springfield Retirement Castle"),
        MapTerm.of("name", "Selma", "address", null),
        MapTerm.of("name", "Otto", "address", null),
        MapTerm.of("name", "Krusty", "address", "Circus")
    );

    @BeforeAll
    public static void setup() throws Exception {
        System.out.println("simpsons = \n" + simpsons);
        System.out.println("residents = \n" + residents);


        connection = DriverManager.getConnection(
                "jdbc:h2:mem:simpsons;DB_CLOSE_DELAY=-1;INIT=CREATE TABLE IF NOT EXISTS person (\"name\" VARCHAR(20), \"age\" INTEGER , \"hair\"  VARCHAR(20), \"weight\" INTEGER, \"gender\"  VARCHAR(20));",
                "sa","");

        try (PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO person (\"name\", \"age\", \"hair\", \"weight\", \"gender\") VALUES (?, ?, ?, ?, ?)")) {
            for (Fact simpson : simpsons) {
                Term s = simpson.getTerm();
                preparedStatement.setString(1, (String) s.get("name"));
                preparedStatement.setInt(2, (Integer) s.get("age"));
                preparedStatement.setString(3, (String) s.get("hair"));
                preparedStatement.setInt(4, (Integer) s.get("weight"));
                preparedStatement.setString(5, (String) s.get("gender"));
                preparedStatement.addBatch();
//                SqlUtil.update(connection, "INSERT INTO person (name, age, hair, weight, gender) VALUES (?, ?, ?, ?, ?)",
//                        List.of(s.get("name"),s.get("age"),s.get("hair"),s.get("weight"),s.get("gender")));

            }
            // Execute batch
            int[] rowsAffected = preparedStatement.executeBatch();
            System.out.println(rowsAffected.length + " rows inserted.");
        }

        dbSimpsons = new JdbcFactSet(connection, Set.of("person"), new UnaryOperation.Identity<>());

    }

    @Test
    public void testSize() {
        assertFalse(simpsons.isEmpty());
        assertFalse(concatSimpsons.isEmpty());
        assertFalse(dbSimpsons.isEmpty());
        assertFalse(residents.isEmpty());
        assertEquals(9, simpsons.size());
        assertEquals(9, dbSimpsons.size());
        assertEquals(9, concatSimpsons.size());
        assertEquals(9, residents.size());
    }

    private long verboseFilterCount(FactSet facts, Predicate<Fact, FactSet> predicate){
        FactSet filtered = facts.filter(predicate);
        System.out.println("Filter " + PrettyPrint.prettyPredicate(predicate) + ":\n" + filtered);
        return filtered.size();
    }

    @Test
    public void remove(){
        assertEquals(9, simpsons.size());
        FactSet grouped = groupByField(simpsons, "gender");
        assertEquals(4, grouped.get("female").size());
        FactSet males = grouped.remove("female");
        assertEquals(5, males.size());

        grouped = groupByField(concatSimpsons, "gender");
        assertEquals(4, grouped.get("female").size());
        males = grouped.remove("female");
        assertEquals(5, males.size());
    }

    @Test
    public void filterGt() {
        assertEquals(6, verboseFilterCount(concatSimpsons, new FieldGt("age", 20)));
        assertEquals(6, verboseFilterCount(simpsons, new FieldGt("age", 20)));
        assertEquals(6, verboseFilterCount(dbSimpsons, new FieldGt("age", 20)));
    }

    @Test
    public void filterNotGt(){
        assertEquals(3, verboseFilterCount(concatSimpsons, new FieldGt("age", 20).not()));
        assertEquals(3, verboseFilterCount(simpsons, new FieldGt("age", 20).not()));
        assertEquals(3, verboseFilterCount(dbSimpsons, new FieldGt("age", 20).not()));
    }

    @Test
    public void filterLt(){
        assertEquals(3, verboseFilterCount(concatSimpsons, new FieldLt("age", 20)));
        assertEquals(3, verboseFilterCount(simpsons, new FieldLt("age", 20)));
        assertEquals(3, verboseFilterCount(dbSimpsons, new FieldLt("age", 20)));
    }

    @Test
    public void filterEq(){
        assertEquals(1, verboseFilterCount(concatSimpsons, new FieldEq("age", 10)));
        assertEquals(1, verboseFilterCount(simpsons, new FieldEq("age", 10)));
        assertEquals(1, verboseFilterCount(dbSimpsons, new FieldEq("age", 10)));
    }

    @Test
    public void filterNeq(){
        assertEquals(8, verboseFilterCount(concatSimpsons, new Comparator.FieldNeq("age", 10)));
        assertEquals(8, verboseFilterCount(simpsons, new Comparator.FieldNeq("age", 10)));
        assertEquals(8, verboseFilterCount(dbSimpsons, new Comparator.FieldNeq("age", 10)));
    }

    @Test
    public void filterAnd(){
        assertEquals(2, verboseFilterCount(concatSimpsons, new FieldGt("age", 20).and(new FieldEq("hair", "short"))));
        assertEquals(2, verboseFilterCount(simpsons, new FieldGt("age", 20).and(new FieldEq("hair", "short"))));
        assertEquals(2, verboseFilterCount(dbSimpsons, new FieldGt("age", 20).and(new FieldEq("hair", "short"))));
    }

    @Test
    public void filterOr(){
        assertEquals(5, verboseFilterCount(concatSimpsons, new FieldLt("age", 10).or(new FieldEq("hair", "short"))));
        assertEquals(5, verboseFilterCount(simpsons, new FieldLt("age", 10).or(new FieldEq("hair", "short"))));
        assertEquals(5, verboseFilterCount(dbSimpsons, new FieldLt("age", 10).or(new FieldEq("hair", "short"))));
    }

    @Test
    public void group(){
        FactSet grouped = groupByField(simpsons, "gender");
        assertEquals(2, Iters.count(grouped.parts()));
        assertEquals(4, grouped.get("female").size());
        grouped = groupByField(concatSimpsons, "gender");
        assertEquals(2, Iters.count(grouped.parts()));
        assertEquals(4, grouped.get("female").size());
        grouped = groupByField(dbSimpsons, "gender");
        assertEquals(2, Iters.count(grouped.parts()));
        assertEquals(4, grouped.get("female").size());
    }

    @Test
    public void distinct(){
        FactSet distinct = duplicateSimpsons.distinct();
        System.out.println("distinct = " + distinct);
        assertEquals(2, distinct.size());
        FactSet distinctConcat = new Concat(duplicateSimpsons, duplicateSimpsons).distinct();
        System.out.println("distinctConcat = " + distinctConcat);
        assertEquals(2, distinctConcat.size());
    }

    @Test
    public void distinctBulk(){
        System.out.println("Perform distinct many times, in order to optimize hashCode and equals on Terms. ");
        FactSet distinct = duplicateSimpsons.distinct();
        System.out.println("distinct = " + distinct);

        int n = 1_00_000; // make bigger

        System.out.println("Executing rule " + n + " times...");
        long t0 = System.currentTimeMillis();
        FactSet distinctConcat = null;
        for (int i = 0; i < n; i++) {
            distinctConcat = new Concat(duplicateSimpsons, duplicateSimpsons).distinct();
        }
        System.out.println("distinctConcat = " + distinctConcat);
        assertEquals(2, distinctConcat.size());

        long t1 = System.currentTimeMillis();
        long t = t1 - t0;
        double tSec = t/1000.0;
        System.out.println("Executing distinct " + n + " times took: " + tSec + " seconds.");
        long rPerSec = Math.round(n / tSec);
        System.out.println("... That is " + rPerSec + " ops/second.");
    }

    @Test
    public void join(){
        FactSet titles = new SinglePartFactSet(List.of(
                new Fact(MapTerm.of("gender", "male", "title", "Mr.")),
                new Fact(MapTerm.of("gender", "female", "title", "Ms."))
        ), "titles");

        System.out.println(titles);
        System.out.println("Joining simpsons and titles:");
        FactSet joined = joinOnField(simpsons, titles, "gender");
        System.out.println("joined = " + joined);
    }

    @Test
    public void named(){
        FactSet johnsons = simpsons.setPart("johnsons");
        assertEquals(johnsons.parts(), Set.of("johnsons"));
        assertEquals(simpsons.parts(), Set.of("simpsons"));

        FactSet filtered = johnsons.filter(new FieldEq("gender", "male"));
        System.out.println("filtered.parts() = " + filtered.parts());
        assertEquals(filtered.parts(), Set.of("johnsons"));
    }

    @Test
    public void setAge(){
        LocalDate dob1 = LocalDate.of(1963, 4, 1);
        final FactSet dob = FactSets.create("dob",
                MapTerm.of("name", "John", "dob", dob1),
                MapTerm.of("name", "Mary", "dob", LocalDate.of(2022, 7, 12))
        );

        final FactSet ages = new ComputeAge("dob", "age").apply(dob);
        Fact first = first(ages);
        System.out.println("first fact = " + first);
        assertEquals(dob1.until(LocalDate.now()).getYears(), first.get("age"));
    }

    @Test
    public void computeAge(){
        final FactSet dob = FactSets.create("dob",
                MapTerm.of("name", "John", "dob", LocalDate.of(1963, 4, 1)),
                MapTerm.of("name", "Mary", "dob", LocalDate.of(2022, 7, 12))
        );

        FactSet dobWithAge = new ComputeAge("dob", "age").apply(dob);
        System.out.println(PrettyPrint.pretty(dobWithAge));
    }
    

    @Test
    public void prettyHomogeneous(){
        System.out.println(PrettyPrint.pretty(simpsons));

        FactSet heterogeneous = new SinglePartFactSet(List.of(
                new Fact(MapTerm.of("color", "red", "name", "strawberry")),
                new Fact(MapTerm.of("price", "10", "name", "ticket"))
        ), "heterogeneous");

        System.out.println(PrettyPrint.pretty(heterogeneous));
    }



    @Test
    public void schema(){
        System.out.println(PrettyPrint.pretty(simpsons));
        System.out.println("Schema:");
        System.out.println(PrettyPrint.pretty(FactSets.schema(simpsons)));
    }


}
