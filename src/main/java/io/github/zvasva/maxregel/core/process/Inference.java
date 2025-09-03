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
                RuleResult result = rule.apply(totalFactSet, tracer);
                totalFactSet = result.output();
                totalUpdate = totalUpdate.union(result.newlyAssigned()).distinct();
                long newSize = totalUpdate.size();
                changed |= newSize > lastUpdateSize;
                lastUpdateSize = newSize;
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
                totalFactSet = rule.apply(totalFactSet, tracer).output();
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
