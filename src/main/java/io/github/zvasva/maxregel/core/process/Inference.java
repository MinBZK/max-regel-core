package io.github.zvasva.maxregel.core.process;

import io.github.zvasva.maxregel.core.factset.FactSet;
import io.github.zvasva.maxregel.core.factset.FactSets;
import io.github.zvasva.maxregel.core.process.rule.*;

import java.util.Collection;

import static io.github.zvasva.maxregel.core.factset.Empty.EMPTY;

/**
 * The Inference class is responsible for deducing new facts from an initial set of facts
 * by iteratively applying a set of rules until no new facts can be inferred or a maximum
 * number of iterations is reached.
 *
 * @author Arvid Halma
 */
public class Inference {


    /**
     * Infers new facts by applying a set of rules iteratively until no more new facts can be inferred
     * or until the maximum number of iterations is reached.
     *
     * @param givenFacts the initial set of facts.
     * @param rules a collection of rules to apply to the factset.
     * @param maxIterations the maximum number of iterations to perform.
     * @return a FactSet containing all inferred facts.
     * @throws RuntimeException if the maximum number of iterations is reached without convergence.
     */
    public static FactSet infer2(FactSet givenFacts, Collection<Rule> rules, Tracer tracer, int maxIterations) {
        FactSet totalUpdate = EMPTY;

        FactSet totalFactSet = givenFacts;
        long lastUpdateSize = 0;

        int i;
        for (i = 0; i < maxIterations; i++) {
            boolean changed = false;
            for (Rule rule : rules) {
//                if(rule instanceof ReturnIf returnIf){
//                    if(returnIf.condition(totalFactSet)){
//                        totalUpdate = returnIf.result(totalFactSet);
//                        changed = false;
//                        break;
//                    }
//                } else if (rule instanceof Script script) {
//                    // recursively infer the script... Then you can ReturnIf
//                    int iterationsLeft = maxIterations - i;
//                    FactSet update = infer2(totalFactSet, script.getRules(), tracer, iterationsLeft);
//                    totalUpdate = totalUpdate.union(update).distinct();
//                    totalFactSet = totalFactSet.union(update); // don't distinct because we don't want to "distinct" the initial data "givenFacts
//                    long newSize = totalUpdate.size();
//                    changed |= newSize > lastUpdateSize;
//                    lastUpdateSize = newSize;
//                } else {
                    RuleResult result = rule.apply(totalFactSet, tracer);
                    totalFactSet = result.total();
                    totalUpdate = totalUpdate.union(result.update()).distinct();
                    long newSize = totalUpdate.size();
                    changed |= newSize > lastUpdateSize;
                    lastUpdateSize = newSize;
//                }
            }
            if(!changed){
                break;
            }
        }

        if (i == maxIterations - 1){
            throw new MaxRegelException("Max iterations reached");
        }
        return totalUpdate;

    }

    /**
     * Infers new facts by applying a set of rules iteratively until no more new facts can be inferred
     * or until the maximum number of iterations is reached.
     *
     * @param givenFacts the initial set of facts.
     * @param rules a collection of rules to apply to the factset.
     * @param maxIterations the maximum number of iterations to perform.
     * @return a FactSet containing all inferred facts.
     * @throws RuntimeException if the maximum number of iterations is reached without convergence.
     */
    public static FactSet infer3(FactSet givenFacts, Collection<Rule> rules, Tracer tracer, int maxIterations) {
        FactSet newFacts = EMPTY;

        FactSet totalFactSet = givenFacts;
        long lastResultSize = 0;

        int i;
        for (i = 0; i < maxIterations; i++) {
            boolean changed = false;
            for (Rule rule : rules) {
                totalFactSet = rule.apply(totalFactSet, tracer).total();
                newFacts = FactSets.partDifference(totalFactSet, givenFacts);
                long newSize = newFacts.size();
                changed |= newSize > lastResultSize;
                lastResultSize = newSize;
            }
            if(!changed){
                break;
            }
        }

        if (i == maxIterations - 1){
            throw new MaxRegelException("Max iterations reached");
        }
        return newFacts;

    }

    public static FactSet backwardChaining(FactSet givenFacts, Script script, Collection<String> goals, Tracer tracer, int maxIterations) {

        AssignmentStructure assignmentStructure = new AssignmentStructure(maxIterations, script);
        FactSet totalFactset = givenFacts;
        for (String goal : goals) {
            if (totalFactset.has(goal)) {
                continue;
            }
            // get the rule for the goal
            Assign rule = assignmentStructure.get(goal);

            // now apply the rule to achieve the goal
            RuleResult result = rule.apply(totalFactset, tracer, assignmentStructure);
            totalFactset = result.total();
        }

        return totalFactset;
    }




        /**
         * Infers new facts by applying a set of rules iteratively until no more new facts can be inferred
         * or until the maximum number of iterations is reached.
         *
         * @param factset the initial set of facts.
         * @param rules a collection of rules to apply to the factset.
         * @param maxIterations the maximum number of iterations to perform.
         * @return a FactSet containing all inferred facts.
         * @throws RuntimeException if the maximum number of iterations is reached without convergence.
         */
    /*public static FactSet infer(FactSet factset, Collection<Rule> rules, Tracer tracer, int maxIterations) {
        FactSet result = EMPTY;

        FactSet totalFactSet = factset;
        boolean changed = true;
        int i = 0;
        while (changed && i < maxIterations) {
            changed = false;
            i++;
            for (Rule rule : rules) {
                FactSet newFactSet = rule.apply(totalFactSet, tracer);
                if (!newFactSet.isEmpty()) {

                    long orgSize = result.size();
                    result = result.union(newFactSet).distinct();
                    changed = changed || result.size() > orgSize;
                    if(changed) {
                        totalFactSet = totalFactSet.union(newFactSet);
                        //System.out.println("totalFactSet = " + totalFactSet);
                    }
                }
            }
        }
        if (i == maxIterations - 1){
           throw new MaxRegelException("Max iterations reached");
        }
        return result;

    }*/
}
