package io.github.zvasva.maxregel.db;

import io.github.zvasva.maxregel.core.factset.FactSet;
import io.github.zvasva.maxregel.core.factset.MultiPartFactSet;
import io.github.zvasva.maxregel.core.factset.SinglePartFactSet;
import io.github.zvasva.maxregel.core.process.AstNode;
import io.github.zvasva.maxregel.core.process.UnaryOperation;
import io.github.zvasva.maxregel.core.process.predicate.Predicate;
import io.github.zvasva.maxregel.core.term.Fact;
import io.github.zvasva.maxregel.core.term.MapTerm;
import io.github.zvasva.maxregel.util.Iters;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static io.github.zvasva.maxregel.core.factset.Empty.EMPTY;

/**
 * A factset backed by an existing SQL database with JDBC support.
 * This allows a database table to be treated as {@link FactSet} in an efficient manner.
 * <p>
 * For some operation, we don't need to retrieve all rows as facts in memory.
 * Take {@link #size()} as an easy example. We don't need all records; but can simply
 * <code>SELECT count(*) FROM table</code>. And, with a bit of effort, filtering using {@link Predicate}s can be mapped
 * into SQL queries too.
 *
 * Getting and removing parts is done lazily: i.e. just returning a reconfigured {@link JdbcFactSet}, making it efficient (O(1)).
 *
 * @author Arvid Halma
 */
public class JdbcFactSet extends MultiPartFactSet {

    private final Connection connection;

    private final Set<String> parts;

    /**
     * Create a factset backed by an existing SQL database.
     * @param connection a DB connection (left open)
     * @param parts the names of the tables, that will form the parts of this factset.
     */
    public JdbcFactSet(Connection connection, Set<String> parts, UnaryOperation<Fact> factOperation) {
        super(List.of(), factOperation);
        this.connection = connection;
        this.parts = parts;
    }

    @Override
    public FactSet setFactOperation(UnaryOperation<Fact> operation) {
        return new JdbcFactSet(connection, parts, operation);
    }

    private Iterator<Fact> iterator(ResultSet resultSet) {
        return new Iters.MappingIterator<>(
                new ResultSetIterator(resultSet),
                map -> factOperation().apply(new Fact(new MapTerm(map))));
    }

    private Iterator<Fact> iterator(String part) {
        ResultSet resultSet = SqlUtil.query(connection, "SELECT * FROM " + part);
        return iterator(resultSet);
    }

    @Override
    public Iterator<Fact> iterator() {
        return Iters.concat(parts().stream().map(this::iterator));
    }

    @Override
    public FactSet get(String part) {
        if(!parts.contains(part)) {
            return EMPTY;
        } else if (parts.size() == 1) {
            // it is already
            return this;
        }
        // Greedy implementation: new SinglePartFactSet(Iters.iterable(iterator(part)), part);
        return new JdbcFactSet(connection, Set.of(part), factOperation());
    }

    @Override
    public Set<String> parts() {
        return parts;
    }

    @Override
    public FactSet setPart(String newName) {
        throw new UnsupportedOperationException("A JdbcFactSet's part can't directly be set. Retrieve (a subset) of data as [Multi|Single]PartFactset first.");
    }

    @Override
    public FactSet remove(String part) {
        if(!parts.contains(part))
            return this;

        Set<String> remainingParts = new HashSet<>(parts);
        remainingParts.remove(part);
        return new JdbcFactSet(connection, remainingParts, factOperation);
    }

    public static String sqlEscape(String s){
        return s.replace('\r', ' ').replace('\n', ' ').replace("'", "''");
    }

    public static String sqlPredicate(Object x) {
        if (x == null) {
            return "NULL";
        }
        if (x instanceof Predicate<?,?> p) {
            return sqlPredicate(p.ast());
        }

        if (!(x instanceof AstNode node)) {
            // number, string
            if(x instanceof Boolean b) {
                return b.toString();
            } else if(x instanceof Number n) {
                return n.toString();
            } else {
                return "'" + sqlEscape(x.toString()) + "'";
            }
        }

        String op = node.op();
        List<?> args = node.args();

        return switch (op) {
            case "not" -> "NOT (" + sqlPredicate(args.getFirst()) + ")";
            case "and" -> sqlPredicate(args.get(0)) + " AND " + sqlPredicate(args.get(1));
            case "or" -> "(" + sqlPredicate(args.get(0)) + " OR " + sqlPredicate(args.get(1)) + ")";
            case "field_eq" -> "(\"" + args.get(0) + "\" = " + sqlPredicate(args.get(1)) + ")";
            case "field_gt" -> "(\"" + args.get(0) + "\" > " + sqlPredicate(args.get(1)) + ")";
            case "field_geq" -> "(\"" + args.get(0) + "\" >= " + sqlPredicate(args.get(1)) + ")";
            case "field_lt" -> "(\"" + args.get(0) + "\" < " + sqlPredicate(args.get(1)) + ")";
            case "field_leq" -> "(\"" + args.get(0) + "\" <= " + sqlPredicate(args.get(1)) + ")";
            // todo: escalate to MultiPartFactSet or implement in SQL
//            case "field_in" -> "(" + sqlPredicate(args.get(0)) + " IN " + sqlPredicate(args.get(1)) + ")";
//            case "field_contains" -> "(" + sqlPredicate(args.get(0)) + " contains " + sqlPredicate(args.get(1)) + ")";
            // case "term_eq" -> "(" + sqlPredicate(args.get(0)) + " = " + sqlPredicate(args.get(1)) + ")";
            default -> throw new IllegalArgumentException("Unsupported operation: " + op);
        };
    }


    public long size(String part) {
        if(!parts.contains(part)) {
            return 0;
        }
        ResultSet resultSet = SqlUtil.query(connection, "SELECT COUNT(*) FROM " + part);
        try {
            resultSet.next();
            return resultSet.getLong(1);
        } catch (SQLException e) {
            return 0;
        }
    }

    @Override
    public long size() {
        return parts().stream().map(this::size).mapToLong(x -> x).sum();
    }

    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    @Override
    public FactSet filter(Predicate<Fact, FactSet> predicate) {
        final String whereCondition = sqlPredicate(predicate);
        return new MultiPartFactSet(parts.stream().map(part -> new SinglePartFactSet(Iters.iterable(iterator(
                SqlUtil.query(connection,"SELECT * FROM "+part+" WHERE " + whereCondition))), part)).collect(Collectors.toList()),
                factOperation());
    }

    @Override
    public boolean any(Predicate<Fact, FactSet> predicate) {
        try {
            for (String part : parts) {
                if (SqlUtil.query(connection, "SELECT 1 AS success FROM " + part + " WHERE " + sqlPredicate(predicate) + " LIMIT 1;").next()) {
                    return true;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e); // todo: or return false?
        }
        return false;
    }

    @Override
    public FactSet distinct() {
        return new MultiPartFactSet(parts.stream().map(part -> new SinglePartFactSet(Iters.iterable(iterator(
                SqlUtil.query(connection,"SELECT DISTINCT * FROM " + part))), part)).collect(Collectors.toList()),
                factOperation());
    }
}
