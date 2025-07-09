package io.github.zvasva.maxregel.core.term;

import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the MapTerm class to verify its correct behavior with various operations.
 * This class includes tests for methods such as get, equals, hashCode, union, and intersection.
 */
class MapTermTest {

    @Test
    void testGetWithExistingKey() {
        // Arrange
        Map<String, Object> map = Map.of("key1", "value1", "key2", "value2");
        MapTerm mapTerm = new MapTerm(map);

        // Act
        Object result = mapTerm.get("key1");

        // Assert
        assertEquals("value1", result);
    }

    @Test
    void testGetWithNonExistingKey() {
        // Arrange
        Map<String, Object> map = Map.of("key1", "value1", "key2", "value2");
        MapTerm mapTerm = new MapTerm(map);

        // Act
        Object result = mapTerm.get("key3");

        // Assert
        assertNull(result);
    }

    @Test
    void testGetWithNullKey() {
        // Arrange
        Map<String, Object> map = Map.of("key1", "value1", "key2", "value2");
        MapTerm mapTerm = new MapTerm(map);

        // Act
        Object result = mapTerm.get(null);

        // Assert
        assertNull(result);
    }

    @Test
    void testGetWithEmptyKey() {
        // Arrange
        Map<String, Object> map = Map.of("key1", "value1", "", "value3");
        MapTerm mapTerm = new MapTerm(map);

        // Act
        Object result = mapTerm.get("");

        // Assert
        assertEquals("value3", result);
    }

    @Test
    public void testEqualsWithDifferentMapOrders() {
        Map<String, String> map1 = new LinkedHashMap<>();
        map1.put("key1", "value1");
        map1.put("key2", "value2");

        Map<String, String> map2 = new LinkedHashMap<>();
        map2.put("key2", "value2");
        map2.put("key1", "value1");

        MapTerm obj1 = new MapTerm(map1);
        MapTerm obj2 = new MapTerm(map2);

        // Test that the two objects are considered equal even though the map orders are different
        assertEquals(obj1, obj2);
    }

    @Test
    public void testHashCodeWithDifferentMapOrders() {
        Map<String, String> map1 = new LinkedHashMap<>();
        map1.put("key1", "value1");
        map1.put("key2", "value2");

        Map<String, String> map2 = new LinkedHashMap<>();
        map2.put("key2", "value2");
        map2.put("key1", "value1");

        MapTerm obj1 = new MapTerm(map1);
        MapTerm obj2 = new MapTerm(map2);

        // Test that the two objects have the same hash code even though the map orders are different
        assertEquals(obj1.hashCode(), obj2.hashCode());
    }

    @Test
    public void testEqualsWithDifferentContent() {
        Map<String, String> map1 = new LinkedHashMap<>();
        map1.put("key1", "value1");
        map1.put("key2", "value2");

        Map<String, String> map2 = new LinkedHashMap<>();
        map2.put("key1", "value1");
        map2.put("key2", "differentValue");

        MapTerm obj1 = new MapTerm(map1);
        MapTerm obj2 = new MapTerm(map2);

        // Test that the two objects are not considered equal because their map contents are different
        assertNotEquals(obj1, obj2);
    }

    @Test
    void testUnionWithNonOverlappingKeys() {
        // Arrange
        Term term1 = MapTerm.of("key1", "value1", "key2", "value2");
        Term term2 = MapTerm.of("key3", "value3", "key4", "value4");

        // Act
        Term resultTerm = Terms.union(term1, term2);

        // Assert
        assertEquals(4, resultTerm.size());
        assertEquals("value1", resultTerm.get("key1"));
        assertEquals("value2", resultTerm.get("key2"));
        assertEquals("value3", resultTerm.get("key3"));
        assertEquals("value4", resultTerm.get("key4"));
    }

    @Test
    void testUnionWithOverlappingKeys() {
        // Arrange
        Term term1 = MapTerm.of("key1", "value1", "key2", "value2");
        Term term2 = MapTerm.of("key2", "newValue2", "key3", "value3");

        // Act
        Term resultTerm = Terms.union(term1, term2);

        // Assert
        assertEquals(3, resultTerm.size());
        assertEquals("value1", resultTerm.get("key1"));
        assertEquals("newValue2", resultTerm.get("key2"));
        assertEquals("value3", resultTerm.get("key3"));
    }

    @Test
    void testUnionWithEmptyTerms() {
        // Arrange
        Term term1 = MapTerm.of();
        Term term2 = MapTerm.of();

        // Act
        Term resultTerm = Terms.union(term1, term2);

        // Assert
        assertTrue(resultTerm.keys().isEmpty());
    }

    @Test
    void testUnionWithFirstTermEmpty() {
        // Arrange
        Term term1 = MapTerm.of();
        Term term2 = MapTerm.of("key1", "value1", "key2", "value2");

        // Act
        Term resultTerm = Terms.union(term1, term2);

        // Assert
        assertEquals(2, resultTerm.size());
        assertEquals("value1", resultTerm.get("key1"));
        assertEquals("value2", resultTerm.get("key2"));
    }

    @Test
    void testUnionWithSecondTermEmpty() {
        // Arrange
        Term term1 = MapTerm.of("key1", "value1", "key2", "value2");
        Term term2 = MapTerm.of();

        // Act
        Term resultTerm = Terms.union(term1, term2);

        // Assert
        assertEquals(2, resultTerm.size());
        assertEquals("value1", resultTerm.get("key1"));
        assertEquals("value2", resultTerm.get("key2"));
    }

    @Test
    void testUnionWithNullValues() {
        // Arrange
        Term term1 = MapTerm.of("key1", "value1", "key2", null);
        Term term2 = MapTerm.of("key3", "value3", "key4", null);

        // Act
        Term resultTerm = Terms.union(term1, term2);

        // Assert
        assertEquals(4, resultTerm.size());
        assertEquals("value1", resultTerm.get("key1"));
        assertNull(resultTerm.get("key2"));
        assertEquals("value3", resultTerm.get("key3"));
        assertNull(resultTerm.get("key4"));
    }



    @Test
    void testIntersectionWithNoCommonKeys() {
        // Arrange
        Term term1 = MapTerm.of("key1", "value1", "key2", "value2");
        Term term2 = MapTerm.of("key3", "value3", "key4", "value4");

        // Act
        Term resultTerm = Terms.intersection(term1, term2);

        // Assert
        assertTrue(resultTerm.keys().isEmpty());
    }

    @Test
    void testIntersectionWithCommonKeysDifferentValues() {
        // Arrange
        Term term1 = MapTerm.of("key1", "value1", "key2", "value2");
        Term term2 = MapTerm.of("key1", "differentValue1", "key2", "differentValue2");

        // Act
        Term resultTerm = Terms.intersection(term1, term2);

        // Assert
        assertTrue(resultTerm.keys().isEmpty());
    }

    @Test
    void testIntersectionWithCommonKeysSameValues() {
        // Arrange
        Term term1 = MapTerm.of("key1", "value1", "key2", "value2");
        Term term2 = MapTerm.of("key1", "value1", "key2", "value2");

        // Act
        Term resultTerm = Terms.intersection(term1, term2);

        // Assert
        assertEquals(2, resultTerm.size());
        assertEquals("value1", resultTerm.get("key1"));
        assertEquals("value2", resultTerm.get("key2"));
    }

    @Test
    void testIntersectionWithMixedCommonKeys() {
        // Arrange
        Term term1 = MapTerm.of("key1", "value1", "key2", "value2", "key3", "value3");
        Term term2 = MapTerm.of("key1", "value1", "key2", "differentValue2", "key4", "value4");

        // Act
        Term resultTerm = Terms.intersection(term1, term2);

        // Assert
        assertEquals(1, resultTerm.size());
        assertEquals("value1", resultTerm.get("key1"));
    }

    @Test
    void testIntersectionWithEmptyTerms() {
        // Arrange
        Term term1 = MapTerm.of();
        Term term2 = MapTerm.of();

        // Act
        Term resultTerm = Terms.intersection(term1, term2);

        // Assert
        assertTrue(resultTerm.keys().isEmpty());
    }

    @Test
    void testIntersectionWithOneTermEmpty() {
        // Arrange
        Term term1 = MapTerm.of("key1", "value1", "key2", "value2");
        Term term2 = MapTerm.of();

        // Act
        Term resultTerm = Terms.intersection(term1, term2);

        // Assert
        assertTrue(resultTerm.keys().isEmpty());
    }



    @Test
    void testPickWithExistingKeys() {
        // Arrange
        Term term = MapTerm.of("key1", "value1", "key2", "value2", "key3", "value3");
        List<String> keysToPick = List.of("key1", "key3");

        // Act
        Term resultTerm = Terms.pick(term, keysToPick);

        // Assert
        assertEquals(2, resultTerm.size());
        assertEquals("value1", resultTerm.get("key1"));
        assertEquals("value3", resultTerm.get("key3"));
        assertFalse(resultTerm.has("key2"));
    }

    @Test
    void testPickWithSomeNonExistingKeys() {
        // Arrange
        Term term = MapTerm.of("key1", "value1", "key2", "value2", "key3", "value3");
        List<String> keysToPick = List.of("key1", "key4");

        // Act
        Term resultTerm = Terms.pick(term, keysToPick);

        // Assert
        assertEquals(1, resultTerm.size());
        assertEquals("value1", resultTerm.get("key1"));
        assertFalse(resultTerm.has("key4"));
    }

    @Test
    void testPickWithAllNonExistingKeys() {
        // Arrange
        Term term = MapTerm.of("key1", "value1", "key2", "value2");
        List<String> keysToPick = List.of("key3", "key4");

        // Act
        Term resultTerm = Terms.pick(term, keysToPick);

        // Assert
        assertTrue(resultTerm.keys().isEmpty());
    }

    @Test
    void testPickWithEmptyKeys() {
        // Arrange
        Term term = MapTerm.of("key1", "value1", "key2", "value2");

        // Act
        Term resultTerm = Terms.pick(term, List.of());

        // Assert
        assertTrue(resultTerm.keys().isEmpty());
    }

    @Test
    void testPickWithAllKeys() {
        // Arrange
        Term term = MapTerm.of("key1", "value1", "key2", "value2", "key3", "value3");
        List<String> keysToPick = List.of("key1", "key2", "key3");

        // Act
        Term resultTerm = Terms.pick(term, keysToPick);

        // Assert
        assertEquals(3, resultTerm.size());
        assertEquals("value1", resultTerm.get("key1"));
        assertEquals("value2", resultTerm.get("key2"));
        assertEquals("value3", resultTerm.get("key3"));
    }
}