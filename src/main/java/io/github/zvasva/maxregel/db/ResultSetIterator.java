package io.github.zvasva.maxregel.db;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * Iterate over an {@link ResultSet}, mapping rows to {@link Map}s of column name, value.
 *
 * @author Arvid Halma
 */
public class ResultSetIterator implements Iterator<Map<String, Object>> {
    private final ResultSet resultSet;
    private final ResultSetMetaData metaData;
    private final int columnCount;
    private boolean hasNext;

    /**
     * Constructor for ResultSetIterator.
     *
     * @param resultSet The ResultSet to iterate over.
     */
    public ResultSetIterator(ResultSet resultSet) {
        try {
            this.resultSet = resultSet;
            this.metaData = resultSet.getMetaData();
            this.columnCount = metaData.getColumnCount();
            this.hasNext = resultSet.next();
        } catch (SQLException e) {
            throw new RuntimeException("Error using ResultSet", e);
        }
    }

    @Override
    public boolean hasNext() {
        return hasNext;
    }

    @Override
    public Map<String, Object> next() {
        if (!hasNext) {
            throw new NoSuchElementException("No more rows in the ResultSet");
        }

        try {
            Map<String, Object> row = new HashMap<>();

            // Map each column to the corresponding value
            for (int i = 1; i <= columnCount; i++) {
                String columnName = metaData.getColumnLabel(i); // Prefer columnLabel for aliases
                Object columnValue = resultSet.getObject(i);
                row.put(columnName, columnValue);
            }

            // Move to the next row
            hasNext = resultSet.next();
            return row;

        } catch (SQLException e) {
            throw new RuntimeException("Error reading ResultSet", e);
        }
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("Remove operation is not supported");
    }
}
