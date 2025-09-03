package io.github.zvasva.maxregel.core.process;

import io.github.zvasva.maxregel.core.factset.FactSet;
import io.github.zvasva.maxregel.core.factset.FactSets;
import io.github.zvasva.maxregel.core.process.rule.NamePrefix;
import io.github.zvasva.maxregel.core.process.rule.Rules;
import io.github.zvasva.maxregel.core.process.rule.Script;
import org.junit.jupiter.api.Test;

import java.sql.SQLOutput;
import java.util.List;

import static io.github.zvasva.maxregel.core.factset.FactSetTest.simpsons;
import static io.github.zvasva.maxregel.core.factset.FactSets.first;
import static io.github.zvasva.maxregel.core.process.rule.Rules.*;
import static io.github.zvasva.maxregel.util.PrettyPrint.print;
import static org.junit.jupiter.api.Assertions.*;

public class ScriptTest {

    @Test
    public void testScript1() {
        print("""
              A simple script
              """);
        Script script = script(
                let("kids", filter("simpsons", "age", "<", 18)),
                let("boys", filter("kids", "gender", "==", "male"))
        );

        print("script", script.ast());
        FactSet result = script.apply(simpsons);
        print("\nresult", result);

        Rules.annotateDependencies(script);
        print("script with deps...", script);
        assertEquals(3, result.get("kids").size());
        assertEquals(1, result.get("boys").size());
    }


    @Test
    public void testScript1Bulk() {
        final int n = 100_000;

        print("""
              A simple script
              """);

        Script script = script(
                let("kids", filter("simpsons", "age", "<", 18)),
                let("boys", filter("kids", "gender", "==", "male"))
        );


        FactSet result = null;
        long t0 = System.currentTimeMillis();
        for (int i = 0; i < n; i++) {
            result = script.apply(simpsons);
        }
        long t1 = System.currentTimeMillis();
        print("\nresult", result);

        System.out.printf("Running script %,d times\n", n);
        System.out.printf("Total time: %.2f sec\n", (t1-t0)/1000.0);
        System.out.printf("Throughput: %,.0f scripts/sec\n", n/((t1-t0)/1000.0));

        Rules.annotateDependencies(script);
        print("script with deps...", script);
        assertEquals(3, result.get("kids").size());
        assertEquals(1, result.get("boys").size());
    }

    @Test
    public void testScriptAppend() {
        print("""
              A script created from two separate scripts.
              """);
        Script script1 = script(
                let("kids", filter("simpsons", "age", "<", 18))
        );
        Script script2 = script(
                let("boys", filter("kids", "gender", "==", "male"))
        );
        Script script = script1.append(script2);

        print("script", script.ast());
        FactSet result = script.apply(simpsons);
        print("\nresult", result);

        Rules.annotateDependencies(script);
        print("script with deps...", script);
        assertEquals(3, result.get("kids").size());
        assertEquals(1, result.get("boys").size());
    }

    @Test
    public void testScriptTempVar() {
        print("""
              A simple script, that just returns some part
              """);
        Script script = script(
                let("local_kids", filter("simpsons", "age", "<", 18)),
                let("boys", filter("local_kids", "gender", "==", "male")),
                from("boys") // "local_kids" is not exported
        );

        print("script", script.ast());
        FactSet result = script.apply(simpsons);
        print("\nresult", result);

        Rules.annotateDependencies(script);
        print("script with deps...", script);

        assertFalse(result.has("local_kids"));
        assertEquals(1, result.get("boys").size());
    }


    @Test
    public void testScriptLocalVar() {
        print("""
              A sub-script, with local parts, that are not exposed to the parent script.
              """);
        Script script = script(
                let("women", filter("simpsons", "gender", "==", "female")),
                let("boys", // boys are renamed to "guys"
                    script(
                        let("kids", filter("simpsons", "age", "<", 18)),
                        let("young_men", filter("kids", "gender", "==", "male")),
                        from("young_men") // "local_kids" is not exported
                )
            )
        );

        print("script", script);
        FactSet result = script.apply(simpsons);
        print("\nresult", result);

        Rules.annotateDependencies(script);
        print("script with deps...", script);
        assertTrue(result.has("women"));
        assertFalse(result.has("kids"));
        assertEquals(1, result.get("boys").size());
    }

    @Test
    public void testScriptLocalVar2() {
        print("""
              A sub-script, with local parts, that are not exposed to the parent script, using *;
              """);
        Script script = script(
                let("women", filter("simpsons", "gender", "==", "female")),
                let("*",
                        script(
                                let("local_kids", filter("simpsons", "age", "<", 18)),
                                let("young_men", filter("local_kids", "gender", "==", "male")),
                                from("young_men") // "local_kids" is not exported
                        )
                )
        );

        Rules.annotateDependencies(script);
        //print(Mermaid.encodeUrl(script));
        print("script", script);

        FactSet result = script.apply(simpsons);
        print("\nresult", result);

        assertTrue(result.has("women"));
        assertTrue(result.has("young_men"));
        assertFalse(result.has("local_kids"));
    }

    @Test
    public void testScriptLocalVarShadows() {
        print("""
                A sub-script, that shadows the parent's script vars.;
                """);
        Script script = script(
                let("old", filter("simpsons", "age", ">", 40)),
                let("very_old",
                        script(
                                let("old", filter("simpsons", "age", ">", 60)),
                                from("old") // "local_kids" is not exported
                        )
                )
        );

        Rules.annotateDependencies(script);
        //print(Mermaid.encodeUrl(script));
        print("script", script);

        FactSet result = script.apply(simpsons);
        print("\nresult", result);

        assertEquals(3, result.get("old").size());
        assertEquals(1, result.get("very_old").size());
        assertFalse(result.has("local_kids"));
    }

    @Test
    public void testScriptSubVarOverwritesGlobalVar() {
        print("""
              A sub-script, that (on * assignment) overwrites a global var.
              """);
        Script script = script(
                let("favorite", filter("simpsons", "name", "==", "Bart")),
                let("*",
                        script(
                                let("favorite",  filter("simpsons", "name", "==", "Lisa")),
                                from("favorite") // "local_kids" is not exported
                        )
                )
        );

        Rules.annotateDependencies(script);
        //print(Mermaid.encodeUrl(script));
        print("script", script);

        FactSet result = script.apply(simpsons);
        print("\nresult", result);

        assertEquals("Lisa", first(result.get("favorite")).get("name"));
    }


    @Test
    public void testScriptSubVarPreventOverwritesGlobalVar() {
        print("""
              A sub-script, that (on * assignment) prevents overwriting a global var, by prefixing the part name
              """);
        Script script = script(
                let("favorite", filter("simpsons", "name", "==", "Bart")),
                let("*", // boys are renamed to "guys"
                        new NamePrefix(
                            script(
                                    let("favorite",  filter("simpsons", "name", "==", "Lisa")),
                                    from("favorite") // "local_kids" is not exported
                            ), "sub_")
                )
        );

        Rules.annotateDependencies(script);
        //print(Mermaid.encodeUrl(script));
        print("script", script);

        FactSet result = script.apply(simpsons);
        print("\nresult", result);

        assertEquals("Bart", first(result.get("favorite")).get("name"));
        assertEquals("Lisa", first(result.get("sub_favorite")).get("name"));
    }

    @Test
    public void testInferSantaLike(){
        Script script = script(
                let("santa_candidates", filter("simpsons", "name", "==", "Santa")),
                let("santa_candidates", sequence(from("old_males"),
                        filter(predicate("hair", "==", "middle")
                                .and(predicate( "weight", ">", 150))))),
                let("old_males", sequence(from("simpsons"), filter(predicate("gender", "==", "male")
                        .and(predicate( "age", ">", 40)))))
        );

        print (script);

        FactSet newFacts = Inference.infer2(simpsons, List.of(script), new Tracer.Assignments(), 10);
        FactSet santas = newFacts.get("santa_candidates");
        print("\nnewFact", FactSets.toStringFull(newFacts));
        print("santas", santas);
        assertFalse(santas.isEmpty());
    }
}
