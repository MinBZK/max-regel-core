package io.github.zvasva.maxregel.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class NumberComparatorTest {

    static NumberComparator numberComparator = new NumberComparator();

    @Test
    public void testEqDD() {
        assertEquals(0, numberComparator.compare(2.0, 2.0));
    }

    @Test
    public void testEqII() {
        assertEquals(0, numberComparator.compare(2, 2));
    }

    @Test
    public void testEqID() {
        assertEquals(0, numberComparator.compare(2, 2.0));
    }

    @Test
    public void testEqDI() {
        assertEquals(0, numberComparator.compare(2, 2.0));
    }

    @Test
    public void testEqINull() {
        assertEquals(0, numberComparator.compare(2, null));
    }

    @Test
    public void testEqIInf() {
        assertEquals(-1, numberComparator.compare(2, Double.POSITIVE_INFINITY));
    }
}
