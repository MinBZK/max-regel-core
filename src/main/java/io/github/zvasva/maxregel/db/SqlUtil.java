package io.github.zvasva.maxregel.db;

import java.sql.*;
import java.util.List;
import java.util.Objects;

/**
 * Helper functions for java.sql objects.
 * @author Arvid Halma
 */
public class SqlUtil {

    public static ResultSet query(Connection connection, String sql) {
        try {
            return statement(connection).executeQuery(sql);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static ResultSet query(Connection connection, String sql, List<?> params) {
        try {
            return statement(connection, sql, params).executeQuery();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static int update(Connection connection, String sql) {
        try {
            return statement(connection).executeUpdate(sql);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static int update(Connection connection, String sql, List<?> params) {
        try {
            return statement(connection, sql, params).executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static Statement statement(Connection connection) {
        /*try (Statement statement = connection.createStatement()) {
            return statement;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }*/

        try {
            return connection.createStatement();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * Fills a PreparedStatement with the given params and executes it.
     *
     * @param connection A DB connection.
     * @param sql        The PreparedStatement to be filled.
     * @param params     A list of params to be set in the PreparedStatement.
     */
    public static PreparedStatement statement(Connection connection, String sql, List<?> params) {
        Objects.requireNonNull(sql);
        Objects.requireNonNull(params);

        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            for (int i = 0; i < params.size(); i++) {
                Object param = params.get(i);

                int i1 = i + 1;
                switch (param) {
                    case null -> statement.setObject(i1, null); // Handle nulls by setting them to SQL NULL with a generic type
                    case Integer n -> statement.setInt(i1, n);
                    case Long n -> statement.setLong(i1, n);
                    case Double x -> statement.setDouble(i1, x);
                    case Float x -> statement.setFloat(i1, x);
                    case Boolean b -> statement.setBoolean(i1, b);
                    case String s -> statement.setString(i1, s);
                    case Date d -> statement.setDate(i1, d);
                    case Timestamp t -> statement.setTimestamp(i1, t);
                    case Time t -> statement.setTime(i1, t);
                    default -> statement.setObject(i1, param); // For other object types, fallback to setObject
                }
            }
            return statement;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
