package io.github.zvasva.maxregel.core.process.rule;

import io.github.zvasva.maxregel.core.factset.FactSet;

/**
 * A tuple of factsets after applying a {@link Rule}.
 *
 * @param output        the factset containing output of a rule
 * @param newlyAssigned the part of the output that are newly declared parts (variables)
 */
public record RuleResult(FactSet output, FactSet newlyAssigned) {
}
