package io.github.zvasva.maxregel.core.process;

import io.github.zvasva.maxregel.core.factset.FactSet;
import io.github.zvasva.maxregel.core.process.rule.Assign;
import io.github.zvasva.maxregel.core.process.rule.BasicRule;
import io.github.zvasva.maxregel.core.process.rule.Rule;
import io.github.zvasva.maxregel.core.process.rule.Rules;
import io.github.zvasva.maxregel.util.Collections;
import io.github.zvasva.maxregel.util.PrettyPrint;

import java.util.List;
import java.util.Map;

/**
 * Updates the metadata of facts after a rule was applied to them.
 *
 * @author Arvid Halma
 */
public interface Tracer {

    void apply(Rule rule, FactSet newFacts);

    default void except(Exception e, Rule rule, FactSet facts) {
        System.err.println("The following statement: " + PrettyPrint.pretty(rule));
        System.err.println("\nThrew exception: " + e.getMessage() );
        System.err.println("\nOn a factset with parts: " + facts.parts() );
        System.err.println();
        e.printStackTrace();
    }

    public static Tracer NONE = new None();
    public static Tracer ASSIGNMENTS = new Assignments();
    public static Tracer FULL = new Full();

    /**
     * Never traces.
     */
    class None implements Tracer {
        @Override
        public void apply(Rule rule, FactSet newFacts) {
            // do nothing
        }
    }

    /**
     * Always traces.
     */
    class Full implements Tracer {
        @Override
        public void apply(Rule rule, FactSet newFacts) {
            newFacts.forEach(fact -> {
                fact.getRules().add(rule);
                fact.setEpoch(fact.getEpoch() + 1);
            });
        }
    }

    /**
     * Only traces assigments.
     */
    class Assignments implements Tracer {
        @Override
        public void apply(Rule rule, FactSet newFacts) {
            boolean assignment = Rules.isAssignment(rule);
            newFacts.forEach(fact -> {
                if(assignment) {
                    //fact.getRules().add(rule);
                    // Add simplified (non-nested) version of this rule.
                    BasicRule r = new BasicRule(rule.op(), rule.info(), (List<Object>) rule.ast().args().stream().filter(a -> !(a instanceof AstNode)).toList());
//                    fact.getRules().add(r);
                    fact.setRules(Collections.union(fact.getRules(), List.of(r)).stream().toList());
                }
                fact.setEpoch(fact.getEpoch() + 1);
            });
        }
    }


    /**
     * Only traces assigments.
     */
    class AssignmentsSimple implements Tracer {
        @Override
        public void apply(Rule rule, FactSet newFacts) {

            final String assignVar = rule instanceof Assign assign ? assign.variable() : null;

            newFacts.forEach(fact -> {
                if(assignVar != null) {
                    //fact.getRules().add(rule);
                    // Add simplified (non-nested) version of this rule.
                    BasicRule r = new BasicRule(rule.op(), Map.of("rule_name", assignVar), List.of());
                    fact.setRules(Collections.union(fact.getRules(), List.of(r)).stream().toList());

                }
                fact.setEpoch(fact.getEpoch() + 1);
            });
        }
    }
    
}
