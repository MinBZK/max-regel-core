package io.github.zvasva.maxregel.core.process;

import io.github.zvasva.maxregel.core.process.rule.Assign;
import io.github.zvasva.maxregel.core.process.rule.Rule;
import io.github.zvasva.maxregel.core.process.rule.Script;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class AssignmentStructure {
    private final int maxIterations;
    private int epoch;

    private final Map<String, Assign> partToAssignment;

    public AssignmentStructure(int maxIterations, Script script) {
        Map<String, Assign> partToAssignment = new LinkedHashMap<>();
        for (Rule rule : script.getRules()) {
            if (rule instanceof Assign assign) {
                String var = assign.variable();
                if (var.equals("*")) {
                    // todo: handle this case
                    throw new MaxRegelException("Wildcard rules are not supported yet when using back chaining");
                }
                if (partToAssignment.containsKey(var)) {
                    throw new MaxRegelException("Variable " + var + " is already defined in the ruleset. Backward chaining does not support multiple rules for the same variable.");
                }
                partToAssignment.put(var, assign);
            }
        }
        this.maxIterations = maxIterations;
        this.partToAssignment = Collections.unmodifiableMap(partToAssignment);
        this.epoch = 0;
    }

    public AssignmentStructure(int maxIterations, Map<String, Assign> partToAssignment) {
        this.maxIterations = maxIterations;
        this.partToAssignment = Collections.unmodifiableMap(partToAssignment);
        this.epoch = 0;
    }

    public boolean exceeded() {
        return maxIterations >= 0 && epoch >= maxIterations;
    }

    public int getEpoch() {
        return epoch;
    }

    public void incrementEpoch() {
        ++epoch;
        if(maxIterations >= 0 && epoch > maxIterations){
            throw new MaxRegelException("Maximum number of iterations exceeded: " + maxIterations);
        }
    }

    public Assign get(String part) {
        return partToAssignment.get(part);
    }
}
