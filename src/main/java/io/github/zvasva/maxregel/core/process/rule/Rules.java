package io.github.zvasva.maxregel.core.process.rule;

import io.github.zvasva.maxregel.core.factset.FactSet;
import io.github.zvasva.maxregel.core.factset.FactSets;
import io.github.zvasva.maxregel.core.process.AstNode;
import io.github.zvasva.maxregel.core.process.predicate.Comparator;
import io.github.zvasva.maxregel.core.process.predicate.Exists;
import io.github.zvasva.maxregel.core.process.predicate.Predicate;
import io.github.zvasva.maxregel.core.process.predicate.Predicates;
import io.github.zvasva.maxregel.core.term.Fact;
import io.github.zvasva.maxregel.core.term.Term;
import io.github.zvasva.maxregel.util.Collections;
import io.github.zvasva.maxregel.util.PrettyPrint;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

import static io.github.zvasva.maxregel.core.factset.Empty.EMPTY;

/**
 * Utilities for creating and manipulating rules to process FactSets.
 * Various types of rules such as assignment, filtering, and composed
 * rules can be created using this class.
 */
public class Rules {

    public static String toString(Rule rule) {
        return rule.ast().toString();
    }

    public static boolean equals(Rule a, Object b) {
        if (b instanceof Rule rb) {
            return a.ast().equals(rb.ast());
        } else if (b instanceof AstNode nb) {
            return a.ast().equals(nb);
        }
        return false;
    }

    public static int hashCode(Rule a) {
        return a == null ? 0 : a.ast().hashCode();
    }

    public static Rule append(String variable, Rule rule) {
        return new AssignUpdate(variable, rule);
    }

    public static Rule let(String variable, Rule rule) {
        return new AssignSet(variable, rule);
    }

    public static Rule let(String variable, Rule rule, Map<String, Object> info) {
        return new AssignSet(variable, rule, info);
    }

    public static Map<String, Object> info(Object... kvs) {
        return Collections.map(kvs);
    }

    public static Rule from(String... parts) {
        /* Avoid always prepending cnst(EMPTY):

        Rule result = cnst(EMPTY);
        for (String part : parts) {
            result = new Concat(result, new From(part));
        }
        return result;
         */
        if (parts.length == 0) {
            return cnst(EMPTY);
        } else if (parts.length == 1) {
            return new From(parts[0]);
        } else {
            Rule result = new From(parts[0]);
            for (int i = 1; i < parts.length; i++) {
                String part = parts[i];
                result = new Concat(result, new From(part));
            }
            return result;
        }
    }

    public static Rule select(String... fields) {
        return new SelectFields(List.of(fields));
    }

    public static Rule cnst(FactSet facts) {
        return new Const(facts);
    }

    public static Rule cnst(Term... terms) {
        return cnst(FactSets.create(terms));
    }

    public static Rule cnst(Object x) {
        return cnst(FactSets.cnst(x));
    }

    public static Rule cnst(String fieldName, Object x) {
        return cnst(FactSets.cnst(fieldName, x));
    }

    public static Rule filter(Predicate<Fact, FactSet> predicate) {
        return new Filter(predicate);
    }

    public static Rule filter(String variable, String operator, Object value) {
        return new Filter(predicate(variable, operator, value));
    }

    public static Rule filter(String part, String variable, String operator, Object value) {
        return new Filter(new From(part), predicate(variable, operator, value));
    }

    public static Rule join(Rule partA, Rule partB, String fieldA, String fieldB) {
        return new Join(partA, partB, fieldA, fieldB);
    }

    public static Rule join(String partA, String partB, String fieldA, String fieldB) {
        return new Join(from(partA), from(partB), fieldA, fieldB);
    }

    public static Predicate<Fact, FactSet> predicate(String variable, String operator, Object value) {
        return switch (operator) {
            case "==" -> new Comparator.FieldEq(variable, value);
            case "!=" -> new Comparator.FieldEq(variable, value).not();
            case ">" -> new Comparator.FieldGt(variable, value);
            case ">=" -> new Comparator.FieldGeq(variable, value);
            case "<" -> new Comparator.FieldLt(variable, value);
            case "<=" -> new Comparator.FieldLeq(variable, value);
            default -> throw new IllegalArgumentException("Unsupported operator: " + operator);
        };
    }

    public static Rule add(String partA, String fieldA, String partB, String fieldB) {
        return new Arithmetic.Add("x", from(partA).then(select(fieldA)), from(partB).then(select(fieldB)));
    }

    public static Rule add(String partA, String partB) {
        return new Arithmetic.Add("x", from(partA), from(partB));
    }

    public static Rule add(String partA, String fieldA, Double x) {
        return new Arithmetic.Add("x", from(partA).then(select(fieldA)), cnst(x));
    }

    public static Rule add(Rule a, Rule b) {
        return new Arithmetic.Add("x", a, b);
    }

    public static Rule sub(Rule a, Rule b) {
        return new Arithmetic.Sub("x", a, b);
    }

    public static Rule sub(String resultsField, Rule a, Rule b) {
        return new Arithmetic.Sub(resultsField, a, b);
    }

    public static Rule mul(Rule a, Rule b) {
        return new Arithmetic.Mul("x", a, b);
    }

    public static Rule div(Rule a, Rule b) {
        return new Arithmetic.Div("x", a, b);
    }

    public static Rule div(String resultsField, Rule a, Rule b) {
        return new Arithmetic.Div(resultsField, a, b);
    }

    public static Rule min(Rule a, Rule b) {
        return new Arithmetic.Min("x", a, b);
    }

    public static Rule min(String resultsField, Rule a, Rule b) {
        return new Arithmetic.Min(resultsField, a, b);
    }

    public static Rule max(Rule a, Rule b) {
        return new Arithmetic.Max("x", a, b);
    }

    public static Rule max(String resultsField, Rule a, Rule b) {
        return new Arithmetic.Max(resultsField, a, b);
    }


    public static Rule sequence(Rule... rules) {
        return Arrays.stream(rules).reduce(new Identity(), Then::new);
    }

    public static Script script(Rule... rules) {
        return new Script(List.of(rules));
    }

    public static Rule factSetCase(FactSetCase.LookupEntry entry, Rule defaultValue, String varName) {
        return new FactSetCase(entry, defaultValue, varName);
    }

    public static Rule factSetCase(List<FactSetCase.LookupEntry> entries, Rule defaultValue, String varName) {
        return new FactSetCase(entries, defaultValue, varName);
    }

    public static FactSetCase.LookupEntry anyThen(Rule select, Rule then) {
        return new FactSetCase.LookupEntry(select, new Exists(), then);
    }


    public static Rule parse(AstNode node) {
        if (node == null) {
            return null;
        }

        final List<?> args = node.args();
        return switch (node.op()) {
            case "assign_update", "+=" -> new AssignUpdate(args.get(0).toString(), parse((AstNode) args.get(1)));
            case "assign_set", "=" -> new AssignSet(args.get(0).toString(), parse((AstNode) args.get(1)));
            case "filter" -> new Filter((Predicate<Fact, FactSet>) Predicates.parse((AstNode) args.get(1)));
            case "from" -> new From(args.get(0).toString());
            case "limit" -> new Limit((Long) args.get(1));
            case "name_prefix" -> new NamePrefix(args.get(1).toString());
            case "remove" -> new Remove(args.get(0).toString());
            case "then" -> new Then(Rules.parse((AstNode) args.get(0)), Rules.parse((AstNode) args.get(1)));
            case "script" -> new Script(args.stream().map(xs -> Rules.parse((AstNode) xs)).toList());

            default -> throw new IllegalArgumentException("Unsupported rule function name: " + node.op());
        };
    }

    public static boolean isAssignment(Rule rule) {
        return rule instanceof Assign;
    }

    public static boolean isAssignment(AstNode node) {
        return isAssignment(node.op());
    }

    public static boolean isAssignment(String op) {
        return "assign_set".equals(op) || "assign_update".equals(op);
    }

    public static void subNodeVisitor(Object obj, final Consumer<AstNode> visit) {
        subNodeVisitor(obj, visit, n -> false);
    }

    /**
     * Visit all AstNodes in the given object, including the object itself, unless skipped.
     * @param obj a(collection or map of) AstNode(s)
     * @param visit called for each AstNode, return true to descend into the node's arguments.
     * @param skip a predicate that returns true for AstNodes that should not be visited.
     */
    public static void subNodeVisitor(Object obj, final Consumer<AstNode> visit, java.util.function.Predicate<AstNode> skip) {
        if (obj instanceof AstNode node) {
            if(!skip.test(node)) {
                visit.accept(node); // self
                node.args().forEach(arg -> subNodeVisitor(arg, visit, skip)); // args
            }
        } else if (obj instanceof Collection<?> c) {
            c.forEach(el -> subNodeVisitor(el, visit, skip));
        } else if (obj instanceof Map<?, ?> m) {
            m.forEach((key, value) -> {
                subNodeVisitor(key, visit, skip);
                subNodeVisitor(value, visit, skip);
            });
        }
    }

    /**
     * Visit all AstNodes in the given object, including the object itself.
     * Let the visitor decide whether to descend into the node's arguments.
     * @param obj a(collection or map of) AstNode(s)
     * @param visit called for each AstNode, return true to descend into the node's arguments.
     */
    public static void subNodeVisitor(Object obj, final Function<AstNode, Boolean> visit) {
        if (obj instanceof AstNode node) {
            boolean descent = visit.apply(node); // self
            if(descent) {
                node.args().forEach(arg -> subNodeVisitor(arg, visit)); // args
            }
        } else if (obj instanceof Collection<?> c) {
            c.forEach(el -> subNodeVisitor(el, visit));
        } else if (obj instanceof Map<?, ?> m) {
            m.forEach((key, value) -> {
                subNodeVisitor(key, visit);
                subNodeVisitor(value, visit);
            });
        }
    }

    /**
     * Update info about this rule's direct dependencies on other rules.
     * The info object will have a "depends_on" list field, that contains the "rule.info.rule_name"s.
     *
     * @param rule the rule (typically a script) that needs to be annotated.
     */
    public static void annotateDependencies(Rule rule) {
        annotateDependencies(rule.ast());
    }

    public static void annotateDependencies(AstNode node) {
        List<String> allVars = new ArrayList<>();
        subNodeVisitor(node, n -> {
            if (isAssignment(n)) {
                String varName = (String) n.args().get(0);
                allVars.add(varName);
                n.info().put("rule_name", varName);
                subNodeVisitor((AstNode) n.args().get(1), bodyNode -> {
                    if ("from".equals(bodyNode.op())) {
                        List<String> dependencies = (List<String>) n.info().get("depends_on");
                        if (dependencies == null) {
                            dependencies = new ArrayList<>();
                        } else {
                            dependencies = new ArrayList<>(dependencies); // ensure mutable
                        }
                        dependencies.add((String) bodyNode.args().get(0)); // the part name
                        n.info().put("depends_on", dependencies);
                    }
                });
            }
        });
    }

    /**
     * Add rule.info.rule_statement that shows the pretty printed form of each assignment.
     *
     * @param rule the rule (typically a script) that needs to be annotated.
     */
    public static void addPrettyRuleInfo(Rule rule) {
        AstNode node = rule.ast();
        subNodeVisitor(node, n -> {
            if (isAssignment(n)) {
                n.info().put("rule_statement", PrettyPrint.pretty(n).trim());
            }
        });
    }

    public static List<Map<String, Object>> getTopLevelInfos(Rule rule) {
        if(rule instanceof Script script) {
            List<Map<String, Object>> result = new ArrayList<>();
            for (Rule r : script.getRules()) {
                result.addAll(getTopLevelInfos(r));
            }
            return result;
        } else {
            if(isAssignment(rule)) {
                return List.of(rule.info());
            } else {
                return List.of();
            }
        }
    }

}
