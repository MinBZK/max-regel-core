package io.github.zvasva.maxregel.util;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.lang.String.format;

/**
 * Render a table in text.
 * @author Arvid Halma
 */
public final class TablePrinter {

    private static final char BORDER_KNOT = '|'; // +
    private static final char HORIZONTAL_BORDER = '-';
    private static final char VERTICAL_BORDER = '|';

    private PrintStream out = System.out;

    private String asNull = ""; // null representation
    private boolean rowSeparator = false; // show horizontal line for each row
    private String cellPadding = " "; // left-right padding
    private int header = 1;  // first n rows
    private int maxColWith = 160;  // first n rows


    public TablePrinter() {}

    public TablePrinter(TablePrinter other) {
        this.asNull = other.asNull;
        this.rowSeparator = other.rowSeparator;
        this.cellPadding = other.cellPadding;
        this.header = other.header;
        this.out = other.out;
    }

    public PrintStream getOut() {
        return out;
    }

    public TablePrinter setOut(PrintStream out) {
        this.out = out;
        return this;
    }

    public String getAsNull() {
        return asNull;
    }

    public TablePrinter setAsNull(String asNull) {
        this.asNull = asNull;
        return this;
    }

    public int getMaxColWith() {
        return maxColWith;
    }

    public TablePrinter setMaxColWith(int maxColWith) {
        this.maxColWith = maxColWith;
        return this;
    }

    public boolean isRowSeparator() {
        return rowSeparator;
    }

    public TablePrinter setRowSeparator(boolean rowSeparator) {
        this.rowSeparator = rowSeparator;
        return this;
    }

    public String getCellPadding() {
        return cellPadding;
    }

    public TablePrinter setCellPadding(String cellPadding) {
        this.cellPadding = cellPadding;
        return this;
    }

    public int getHeader() {
        return header;
    }

    public TablePrinter setHeader(int header) {
        this.header = header;
        return this;
    }

    public String toString(Map<Object, Object> table) {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (PrintStream ps = new PrintStream(baos, true, StandardCharsets.UTF_8)) {
            new TablePrinter(this).setOut(ps).print(table.entrySet().stream().map(e -> List.of(e.getKey(), e.getValue())).toList());
        }
        return baos.toString(StandardCharsets.UTF_8);
    }

    public String toString(List<List<Object>> table) {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (PrintStream ps = new PrintStream(baos, true, StandardCharsets.UTF_8)) {
            new TablePrinter(this).setOut(ps).print(table);
        }
        return baos.toString(StandardCharsets.UTF_8);
    }

    public String mapsToString(List<Map> table) {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (PrintStream ps = new PrintStream(baos, true, StandardCharsets.UTF_8)) {
            new TablePrinter(this).setOut(ps).printMaps(table);
        }
        return baos.toString(StandardCharsets.UTF_8);
    }

    public void printMaps(List<Map> table) {
        int n = table.size();
        if (header == 1) {
            n++;
        }

        Object[][] objects = new Object[n][];
        Set keys = new LinkedHashSet<>();
        table.forEach(map -> map.keySet().stream().map(Object::toString).forEach(keys::add));
        if (header == 1) {
            objects[0] = keys.toArray();
        }
        int i = header == 1 ? 1 : 0;
        for (Map<?, ?> map : table) {
            objects[i++] = keys.stream().map(map::get).toArray();
        }
        print(objects);
    }

    public void print(List<List<Object>> table) {
        Object[][] objects = new Object[table.size()][];
        for (int i = 0, tableSize = table.size(); i < tableSize; i++) {
             objects[i] = table.get(i).toArray();
        }
        print(objects);
    }

    public void print(Object[][] table) {
        if ( table == null ) {
            throw new IllegalArgumentException("No tabular data provided");
        }
        if ( table.length == 0 ) {
            return;
        }
        final int[] widths = new int[getMaxColumns(table)];
        adjustColumnWidths(table, widths);
        printPreparedTable(table, widths, getHorizontalBorder(widths));
    }

    private void printPreparedTable(Object[][] table, int[] widths, String horizontalBorder) {
        final int lineLength = horizontalBorder.length();
//        out.println(horizontalBorder);
        for (int i = 0; i < table.length; i++) {
            Object[] row = table[i];
            if (row != null) {
                out.println(getRow(row, widths, lineLength));
                if (rowSeparator || header-1  == i)
                    out.println(horizontalBorder);
            }
        }
//        if (!rowSeparator)
//            out.println(horizontalBorder);
    }

    private String getRow(Object[] row, int[] widths, int lineLength) {
        final StringBuilder builder = new StringBuilder(lineLength).append(VERTICAL_BORDER);
        final int maxWidths = widths.length;
        for ( int i = 0; i < maxWidths; i++ ) {
            String value = getCellValue(safeGet(row, i, null));
            if(maxColWith > 0 && value.length() > maxColWith) {
                value = value.substring(0, maxColWith - 2) + "…";
            }
            builder.append(padRight(value, widths[i])).append(VERTICAL_BORDER);
        }
        return builder.toString();
    }

    private String getHorizontalBorder(int[] widths) {
        final StringBuilder builder = new StringBuilder(256);
        builder.append(BORDER_KNOT);
        for ( final int w : widths ) {
            builder.append(String.valueOf(HORIZONTAL_BORDER).repeat(Math.max(0, w)));
            builder.append(BORDER_KNOT);
        }
        return builder.toString();
    }

    private int getMaxColumns(Object[][] rows) {
        int max = 0;
        for ( final Object[] row : rows ) {
            if ( row != null && row.length > max ) {
                max = row.length;
            }
        }
        return max;
    }

    private void adjustColumnWidths(Object[][] rows, int[] widths) {
        for ( final Object[] row : rows ) {
            if ( row != null ) {
                for ( int c = 0; c < widths.length; c++ ) {
                    final String cv = getCellValue(safeGet(row, c, asNull));
                    final int l = cv.length();
                    if ( widths[c] < l ) {
                        widths[c] = maxColWith > 0 ? Math.min(l, maxColWith) : l;
                    }
                }
            }
        }
    }

    private static String padRight(String s, int n) {
        return format("%1$-" + n + "s", s);
    }

    private String safeGet(Object[] array, int index, String defaultValue) {
        if (index < array.length) {
            Object a = array[index];
            return a == null ? asNull : a.toString();
        }
        return defaultValue;
    }

    private String getCellValue(Object value) {
        return cellPadding + (value == null ? asNull : value.toString()) + cellPadding;
    }

}
