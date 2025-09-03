package io.github.zvasva.maxregel.core.process.rule;

import io.github.zvasva.maxregel.core.factset.FactSet;

/**
 * A tuple of factsets after applying a {@link Rule}.
 * @param update the newly created factset by the rule
 * @param total the factset containing the all new data to continue with for a next rule application.
 * @param totalChanged indicate if the total factset has changed.
 */
public record RuleResult(FactSet update, FactSet total, boolean totalChanged) {
}
