package io.github.zvasva.maxregel.util;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Comparator;

/**
 * Comparator for Integer, Float, Double, ....
 * Undetermined comparisons (NaN, null, other type) are equal to 0, as far as numbers care.
 *
 * Also note that numbers will be converted to doubles. The smallest long value that cannot be exactly represented as a double is:
 * <code>
 * 2^53 + 1 = 9,007,199,254,740,993
 * </code>
 * So, long values greater than this bound will misbehave. A solution is to convert them to {@link BigDecimal}s, but this is computationally expensive.
 *
 * @author Arvid Halma
 */
public class NumberComparator implements Comparator<Number> {

    public int compare(Number x, Number y) {
        return cmp(x, y);
    }

    public static int cmp(final Number x, final Object y) {
        if (x == null || y == null) {
            return 0;
        }
        if  (!(y instanceof Number ny)) {
            return 0;
        }
        return Double.compare(x.doubleValue(), ny.doubleValue());
        /*if (isSpecial(x) || isSpecial(ny))
            return Double.compare(x.doubleValue(), ny.doubleValue());
        else
            return toBigDecimal(x).compareTo(toBigDecimal(ny));*/
    }

    private static boolean isSpecial(Number n) {
        var specialDouble = n instanceof Double d
                && (Double.isNaN(d) || Double.isInfinite(d));
        var specialFloat = n instanceof Float f
                && (Float.isNaN(f) || Float.isInfinite(f));
        return specialDouble || specialFloat;
    }

    private static BigDecimal toBigDecimal(Number number) {
        if(number == null) {
            return BigDecimal.ZERO;
        }
        if (number instanceof BigDecimal d)
            return d;
        if (number instanceof BigInteger i)
            return new BigDecimal(i);
        if (number instanceof Byte || number instanceof Short
                || number instanceof Integer || number instanceof Long)
            return new BigDecimal(number.longValue());
        if (number instanceof Float || number instanceof Double)
            return new BigDecimal(number.doubleValue());

        try {
            return new BigDecimal(number.toString());
        } catch(NumberFormatException e) {
            return BigDecimal.ZERO;
        }
    }

}
