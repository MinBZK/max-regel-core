package io.github.zvasva.maxregel.core.process;

import java.util.List;
import java.util.Map;

/**
 * Abstract syntax tree node. A value or a function call
 * @param op operator identifier (function name)
 * @param info meta data for this expression (like 'source')
 * @param args the arguments for this call
 *
 * @author Arvid Halma
 */
public record AstNode(String op, Map<String, Object> info, List<?> args) {}
