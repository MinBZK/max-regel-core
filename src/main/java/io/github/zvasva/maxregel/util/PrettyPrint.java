package io.github.zvasva.maxregel.util;

import io.github.zvasva.maxregel.core.factset.FactSet;
import io.github.zvasva.maxregel.core.factset.FactSets;
import io.github.zvasva.maxregel.core.process.AstNode;
import io.github.zvasva.maxregel.core.process.predicate.Predicate;
import io.github.zvasva.maxregel.core.process.rule.Rule;
import io.github.zvasva.maxregel.core.term.Term;
import io.github.zvasva.maxregel.core.term.Terms;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Utility class for generating pretty-printed representations of predicates and fact sets.
 */
public class PrettyPrint {

    ///// toString functions /////

    public static String pretty(Rule rule) {
        return pretty(rule.ast());
    }

    public static String pretty(Term term) {
        return Terms.toString(term);
    }


    public static String pretty(Predicate<?, ?> predicate) {
        return prettyPredicate(predicate.ast());
    }

    public static String pretty(Object object) {
        return pretty(object, "");
    }

    public static String pretty(Object object, String indent) {
        return object == null ? "NULL" : switch (object) {
            case Rule rule -> pretty(rule);
            case AstNode node -> pretty(node, indent);
            case FactSet fs -> pretty(fs);
            case Term term -> pretty(term);
            default -> Objects.toString(object);
        };
    }

    private static String prettyArithmetic(AstNode ast) {
        return pretty((AstNode) ast.args().get(1)) + " " + prettyArithmeticOp(ast) + " " +  pretty((AstNode) ast.args().get(2));
    }

    private static String prettyCaseLookup(List<List<AstNode>> lookup) {
        return lookup.stream().map(
                entry -> prettyPredicate(entry.get(0)) + " -> " + pretty(entry.get(1))
        ).collect(Collectors.joining("; "));
    }

    private static String prettyCaseLookup3(List<List<AstNode>> lookup, String indent) {
        return lookup.stream().map(
                entry -> "  (" + pretty(entry.get(0)) + " >> " +  prettyPredicate(entry.get(1)) + ") THEN " + pretty((AstNode) entry.get(2)))
            .collect(Collectors.joining("\n" + indent));
    }

    private static String prettyArithmeticOp(AstNode ast) {
        return switch (ast.op()) {
            case "add" -> "+";
            case "sub" -> "-";
            case "mul" -> "*";
            case "div" -> "/";
            case "pow" -> "^";
            default -> ast.op();
        };
    }

    private static String defaultPretty(AstNode ast) {
        return defaultPretty(ast, "");
    }

    private static String defaultPretty(AstNode ast, String indent) {
        String op = ast.op();
        List<?> args = ast.args();
        if(args.isEmpty()) {
            return op;
        } else if(args.get(0) instanceof AstNode firstNode && "identity".equals(firstNode.op())) {
            args = args.subList(1, args.size()); // skip first "identity" select args
        }
//        return op + args.stream().map(object -> pretty(object, indent)).toList();
        return op + " " + args.stream().map(object -> pretty(object, indent)).collect(Collectors.joining(" "));
    }

    public static String pretty(AstNode ast) {
        return pretty(ast, "");
    }

    public static String pretty(AstNode ast, String indent) {
        String op = ast.op();
        List<?> args = ast.args();
        Map<String, Object> info = ast.info();
        return switch (op) {
            case "identity" -> "identity";
            case "script" -> args.stream().map(n -> pretty((AstNode)n, indent)).collect(Collectors.joining("\n" + indent));
//            case "cached" -> "cached[" + pretty((AstNode) args.get(0), indent) + "]";
            case "concat" -> args.stream().map(n -> pretty((AstNode)n)).collect(Collectors.joining(" & "));
            case "then" -> {
                if (((AstNode) args.get(0)).op().equals("identity")) {
                    yield pretty((AstNode) args.get(1), indent);
                } else if (((AstNode) args.get(1)).op().equals("identity")) {
                    yield pretty((AstNode) args.get(0), indent);
                } else {
                    yield pretty((AstNode) args.get(0), indent) + "; " + pretty((AstNode) args.get(1), indent);
//                    yield pretty((AstNode) args.get(0), indent) + " >> " + pretty((AstNode) args.get(1), indent);
                }
            }
            case "assign_set", "assign_update" -> {
                String init = "";
                if(info.size() > 1) {
                    init = "\n"+indent+"# info: {" + info.entrySet().stream().map(entry -> entry.getKey() + ": \"" + entry.getValue() + "\"").collect(Collectors.joining(", ")) + "}\n";
                }
                yield init + indent + args.get(0) + ("assign_set".equals(op) ? " = " : " += ") + pretty((AstNode) args.get(1), indent + "  ");
            }
            case "case" -> "case[ " + prettyCaseLookup((List<List<AstNode>>) args.get(1)) +  " ELSE " + pretty( (AstNode) args.get(2), indent) + " ] AS " + args.get(3);
            case "factsetcase" -> "fscase[\n"+indent+"  IF\n" + indent + "  " + prettyCaseLookup3((List<List<AstNode>>) args.get(0), indent + "  ") +  " \n"+indent+"  ELSE " + pretty( (AstNode) args.get(1)) + "\n"+indent+"]";
            case "add", "sub", "mul", "div", "pow" ->  prettyArithmetic(ast) + " AS " + args.get(0);
            case "min", "max" -> prettyArithmeticOp(ast) + "["+ pretty(args.get(1)) + ", " + pretty(args.get(2)) + " ]";
            case "aggregate_min", "aggregate_max", "aggregate_sum" -> op.substring(10) + "["+ pretty(args.get(0)) + " ]";
            case "compare" -> "compare[" + prettyPredicate((String)args.get(1), List.of(args.get(2), args.get(3))) + "] AS " + args.get(0);
            case "const" -> Objects.toString(FactSets.value((FactSet) args.get(0)));
            case "join" -> "join[\n" +  pretty((AstNode)args.get(0), indent) + ", " + pretty((AstNode)args.get(1), indent) + "] ON\n" + args.get(2) + " == " + args.get(3) + "\n]";
            case "select", "count", "limit", "name_prefix", "print" -> PrettyPrint.defaultPretty(ast, indent);
            default -> defaultPretty(ast);
        };
    }

    public static String prettyPredicate(Object x) {
        if (x == null) {
            return "NULL";
        }

        String op;
        List<?> args;
        switch (x) {
            case AstNode node -> {
                op = node.op();
                args = node.args();
            }
            case Predicate<?,?> p -> {
                AstNode node = p.ast();
                op = node.op();
                args = node.args();
            }
            case List<?> list -> {
                op = list.getFirst().toString();
                args = list.subList(1, list.size());
            }
            default -> {
                // number, string
                return x.toString();
            }
        }
        return prettyPredicate(op, args);
    }

    public static String prettyPredicate( String op, List<?> args) {
        return switch (op) {
            case "not" -> "NOT (" + prettyPredicate(args.getFirst()) + ")";
            case "and" -> prettyPredicate(args.get(0)) + " AND " + prettyPredicate(args.get(1));
            case "or" -> "(" + prettyPredicate(args.get(0)) + " OR " + prettyPredicate(args.get(1)) + ")";
            case "field_eq" -> "(" + prettyPredicate(args.get(0)) + " == " + pretty(args.get(1)) + ")";
            case "field_neq" -> "(" + prettyPredicate(args.get(0)) + " != " + pretty(args.get(1)) + ")";
            case "field_gt" -> "(" + prettyPredicate(args.get(0)) + " > " + pretty(args.get(1)) + ")";
            case "field_geq" -> "(" + prettyPredicate(args.get(0)) + " >= " + pretty(args.get(1)) + ")";
            case "field_lt" -> "(" + prettyPredicate(args.get(0)) + " < " + pretty(args.get(1)) + ")";
            case "field_leq" -> "(" + prettyPredicate(args.get(0)) + " <= " + pretty(args.get(1)) + ")";
            case "field_in" -> "(" + prettyPredicate(args.get(0)) + " IN " + pretty(args.get(1)) + ")";
            case "field_contains" -> "(" + prettyPredicate(args.get(0)) + " contains " + pretty(args.get(1)) + ")";
            case "term_eq" -> "(" + prettyPredicate(args.get(0)) + " AND " + prettyPredicate(args.get(1)) + ")";
            default -> op + args.stream().map(PrettyPrint::prettyPredicate).toList();
        };
    }


    /**
     * Generates a pretty-printed Markdown table representation of the provided FactSet.
     * Note: this only works nicely for FactSets with homogeneous parts, i.e. parts with facts that have the same fields.
     *
     * @param factset the FactSet to be pretty-printed.
     * @return a string that represents the pretty-printed FactSet.
     */
    public static String pretty(FactSet factset) {
        return Iters.stream(factset.parts()).map(part -> pretty(factset.get(part), 10, part) ).collect(Collectors.joining("\n", "---\n", "---"));
    }

    /**
     * Generates a pretty-printed Markdown table representation of the provided FactSet.
     * Note: this only works nicely for FactSets with homogeneous parts, i.e. parts with facts that have the same fields.
     *
     * @param factset the FactSet to be pretty-printed.
     * @param limit maximum number of rows.
     * @return a string that represents the pretty-printed FactSet.
     */
    public static String pretty(FactSet factset, int limit) {
        return Iters.stream(factset.parts()).map(part -> pretty(factset.get(part), limit, part) ).collect(Collectors.joining("\n", "---\n", "---"));
    }

    private static String pretty(FactSet facts, long limit, String part) {
        if(limit < 0) {
            limit = Long.MAX_VALUE;
        }

        @SuppressWarnings("rawtypes")
        List<Map> rows = facts.stream().limit(limit).map(f -> (Map)Terms.asMap(f.getTerm())).toList();
        String table = new TablePrinter().mapsToString(rows);

        final long n = facts.size();
        if(n > limit){
            table += "... + " + (n - limit) + " additional rows\n";
        }
        return "### " + part + "\n\n" + table;
    }

    ///// print functions /////

    public static void print(Predicate<?,?> predicate) {
        System.out.println(pretty(predicate));
    }

    public static void print(Rule rule) {
        System.out.println(pretty(rule));
    }

    public static void print(FactSet factset) {
        System.out.println(pretty(factset));
    }

    public static void print(FactSet factset, int limit) {
        System.out.println(pretty(factset, limit));
    }

    public static void print(Term term) {
        System.out.println(pretty(term));
    }

    public static void print(Object ... objects) {
        for (Object object : objects) {
            System.out.println(pretty(object));
        }
    }

}
