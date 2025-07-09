package io.github.zvasva.maxregel.util;

import java.util.Collection;
import java.util.Map;

/**
 * Convert values into other types. Like casting but, more fuzzy... JS fuzzy.
 * This is useful for converting values from JSON or other sources.
 *
 * @author Arvid Halma
 */
public class Coerce {

    //////////////// Boolean ////////////////

    /**
     * Convert an object to a boolean value.
     * @param x the object to convert
     * @return true if the object is not null and has a truthy value, false otherwise
     */
    public static boolean asBoolean(Object x) {
        return switch (x) {
            case null -> false;
            case Boolean b -> b;
            case String s -> !s.isBlank() && !"false".equalsIgnoreCase(s);
            case Number n -> {
                double d = n.doubleValue();
                yield !Double.isNaN(d) && d != 0.0;
            }
            case Collection c -> !c.isEmpty();
            case Map m -> !m.isEmpty();
            default -> true;
        };
    }

    /**
     * Convert an array of objects to a boolean value.
     * @param xs the array of objects
     * @return true if the array is not null and has at least one element, false otherwise
     */
    public static boolean asBoolean(Object[] xs){
        return xs != null && xs.length > 0;
    }


    /**
     * Find the first object in the array that is truthy.
     * @param ts the array of objects
     * @param <T> the type of the objects
     * @return the first truthy object, or null if none are found
     */
    public static <T> T firstTrue(T ... ts){
        for (T t : ts) {
            if(asBoolean(t))
                return t;
        }
        return null;
    }

    /**
     * Return the first truthy object or the second object if the first is falsy.
     * @param a the first object
     * @param b the second object
     * @param <T> the type of the objects
     * @return a if it is truthy, otherwise b
     */
    public static <T> T orElse(T a, T b) {
        return asBoolean(a) ? a : b;
    }

    //////////////// Integer-like ////////////////

    /**
     * Convert an object to a Long value.
     * @param x the object to convert
     * @return the Long value or null if conversion fails
     */
    public static Long asLong(Object x){
        return asLong(x, null);
    }

    /**
     * Convert an object to a Long value, with a default value if conversion fails.
     * @param x the object to convert
     * @param defaultVal the default value to return if conversion fails
     * @return the Long value or the default value if conversion fails
     */
    public static Long asLong(Object x, Number defaultVal){
        if(x == null)
            return defaultVal == null ? null : defaultVal.longValue();
        if(x instanceof Number n)
            return n.longValue();
        if(x instanceof String s) {
            try {
                return Double.valueOf(s).longValue();
            } catch (NumberFormatException e) {
                return defaultVal == null ? null : defaultVal.longValue();
            }
        }
        return defaultVal == null ? null : defaultVal.longValue();
    }

    /**
     * Convert an object to a long primitive value.
     * @param x the object to convert
     * @return the long primitive value or 0 if conversion fails
     */
    public static long asLongPrimitive(Object x){
        return asLongPrimitive(x, 0);
    }

    /**
     * Convert an object to a long primitive value, with a default value if conversion fails.
     * @param x the object to convert
     * @param defaultVal the default value to return if conversion fails
     * @return the long primitive value or the default value if conversion fails
     */
    public static long asLongPrimitive(Object x, Number defaultVal){
        final Long y = asLong(x, defaultVal);
        return y == null ? 0L : y.longValue();
    }

    /**
     * Convert an object to an Integer value.
     * @param x the object to convert
     * @return the Integer value or null if conversion fails
     */
    public static Integer asInteger(Object x){
        return asInteger(x, null);
    }

    /**
     * Convert an object to an Integer value, with a default value if conversion fails.
     * @param x the object to convert
     * @param defaultVal the default value to return if conversion fails
     * @return the Integer value or the default value if conversion fails
     */
    public static Integer asInteger(Object x, Number defaultVal){
        final Long y = asLong(x, defaultVal);
        return y == null ? null : y.intValue();
    }

    /**
     * Convert an object to an int primitive value.
     * @param x the object to convert
     * @return the int primitive value or 0 if conversion fails
     */
    public static int asIntPrimitive(Object x){
        return asIntPrimitive(x, 0);
    }

    /**
     * Convert an object to an int primitive value, with a default value if conversion fails.
     * @param x the object to convert
     * @param defaultVal the default value to return if conversion fails
     * @return the int primitive value or the default value if conversion fails
     */
    public static int asIntPrimitive(Object x, Number defaultVal){
        final Long y = asLong(x, defaultVal);
        return y == null ? 0 : y.intValue();
    }

    /**
     * Convert an object to a Short value.
     * @param x the object to convert
     * @return the Short value or null if conversion fails
     */
    public static Short asShort(Object x){
        return asShort(x, null);
    }

    /**
     * Convert an object to a Short value, with a default value if conversion fails.
     * @param x the object to convert
     * @param defaultVal the default value to return if conversion fails
     * @return the Short value or the default value if conversion fails
     */
    public static Short asShort(Object x, Number defaultVal){
        final Long y = asLong(x, defaultVal);
        return y == null ? null : y.shortValue();
    }

    /**
     * Convert an object to a short primitive value.
     * @param x the object to convert
     * @return the short primitive value or 0 if conversion fails
     */
    public static short asShortPrimitive(Object x){
        return asShortPrimitive(x, 0);
    }

    /**
     * Convert an object to a short primitive value, with a default value if conversion fails.
     * @param x the object to convert
     * @param defaultVal the default value to return if conversion fails
     * @return the short primitive value or the default value if conversion fails
     */
    public static short asShortPrimitive(Object x, Number defaultVal){
        final Long y = asLong(x, defaultVal);
        return y == null ? 0 : y.shortValue();
    }

    /**
     * Convert an object to a Byte value.
     * @param x the object to convert
     * @return the Byte value or null if conversion fails
     */
    public static Byte asByte(Object x){
        return asByte(x, null);
    }

    /**
     * Convert an object to a Byte value, with a default value if conversion fails.
     * @param x the object to convert
     * @param defaultVal the default value to return if conversion fails
     * @return the Byte value or the default value if conversion fails
     */
    public static Byte asByte(Object x, Number defaultVal){
        final Long y = asLong(x, defaultVal);
        return y == null ? null : y.byteValue();
    }

    /**
     * Convert an object to a byte primitive value.
     * @param x the object to convert
     * @return the byte primitive value or 0 if conversion fails
     */
    public static byte asBytePrimitive(Object x){
        return asBytePrimitive(x, 0);
    }

    /**
     * Convert an object to a byte primitive value, with a default value if conversion fails.
     * @param x the object to convert
     * @param defaultVal the default value to return if conversion fails
     * @return the byte primitive value or the default value if conversion fails
     */
    public static byte asBytePrimitive(Object x, Number defaultVal){
        final Long y = asLong(x, defaultVal);
        return y == null ? 0 : y.byteValue();
    }

    //////////////// Double-like////////////////

    /**
     * Convert an object to a Double value.
     * @param x the object to convert
     * @return the Double value or null if conversion fails
     */
    public static Double asDouble(Object x){
        return asDouble(x, null);
    }

    /**
     * Convert an object to a Double value, with a default value if conversion fails.
     * @param x the object to convert
     * @param defaultVal the default value to return if conversion fails
     * @return the Double value or the default value if conversion fails
     */
    public static Double asDouble(Object x, Number defaultVal){
        if(x == null)
            return defaultVal == null ? null : defaultVal.doubleValue();
        if(x instanceof Number n)
            return n.doubleValue();
        if(x instanceof String s) {
            try {
                if(s.endsWith("%")){
                    s = s.substring(0, s.length()-1);
                }
                return Double.valueOf(s);
            } catch (NumberFormatException e) {
                return defaultVal == null ? null : defaultVal.doubleValue();
            }
        }
        return defaultVal == null ? null : defaultVal.doubleValue();
    }

    /**
     * Convert an object to a double primitive value.
     * @param x the object to convert
     * @return the double primitive value or NaN if conversion fails
     */
    public static double asDoublePrimitive(Object x){
        return asDoublePrimitive(x, Double.NaN);
    }

    /**
     * Convert an object to a double primitive value, with a default value if conversion fails.
     * @param x the object to convert
     * @param defaultVal the default value to return if conversion fails
     * @return the double primitive value or the default value if conversion fails
     */
    public static double asDoublePrimitive(Object x, double defaultVal){
        final Double d = asDouble(x, null);
        return d == null ? defaultVal : d;
    }

    /**
     * Convert an object to a Float value.
     * @param x the object to convert
     * @return the Float value or null if conversion fails
     */
    public static Float asFloat(Object x){
        return asFloat(x, null);
    }

    /**
     * Convert an object to a Float value, with a default value if conversion fails.
     * @param x the object to convert
     * @param defaultVal the default value to return if conversion fails
     * @return the Float value or the default value if conversion fails
     */
    public static Float asFloat(Object x, Number defaultVal){
        final Long y = asLong(x, defaultVal);
        return y == null ? null : y.floatValue();
    }

    /**
     * Convert an object to a float primitive value.
     * @param x the object to convert
     * @return the float primitive value or 0f if conversion fails
     */
    public static float asFloatPrimitive(Object x){
        return asFloatPrimitive(x, null);
    }

    /**
     * Convert an object to a float primitive value, with a default value if conversion fails.
     * @param x the object to convert
     * @param defaultVal the default value to return if conversion fails
     * @return the float primitive value or the default value if conversion fails
     */
    public static float asFloatPrimitive(Object x, Character defaultVal){
        final Float d = asFloat(x, null);
        return d == null ? (defaultVal == null ? 0f : defaultVal) : d;
    }

    //////////////// String ////////////////

    /**
     * Convert an object to a String value.
     * @param x the object to convert
     * @return the String value or null if conversion fails
     */
    public static String asString(Object x){
        return asString(x, null);
    }

    /**
     * Convert an object to a String value, with a default value if conversion fails.
     * @param x the object to convert
     * @param defaultVal the default value to return if conversion fails
     * @return the String value or the default value if conversion fails
     */
    public static String asString(Object x, String defaultVal){
        if(x == null)
            return defaultVal;
        return x.toString();
    }

    /**
     * Convert an object to a Character.
     * @param x the object to convert
     * @return a Character or null if conversion fails
     */
    public static Character asCharacter(Object x){
        return asCharacter(x, null);
    }

    /**
     * Convert an object to a Character, with a default value if conversion fails.
     * @param x the object to convert
     * @param defaultVal the default value to return if conversion fails
     * @return a Character or the default value if conversion fails
     */
    public static Character asCharacter(Object x, Character defaultVal){
        if(x == null)
            return defaultVal;
        else if(x instanceof Character y)
            return y;
        else {
            String s = x.toString();
            return s.isEmpty() ? null : s.charAt(0);
        }
    }

    /**
     * Convert an object to a char primitive value.
     * @param x the object to convert
     * @return the char primitive value or 0 if conversion fails
     */
    public static char asCharPrimitive(Object x){
        return asCharPrimitive(x, null);
    }

    /**
     * Convert an object to a char primitive value, with a default value if conversion fails.
     * @param x the object to convert
     * @param defaultVal the default value to return if conversion fails
     * @return the char primitive value or the default value if conversion fails
     */
    public static char asCharPrimitive(Object x, Character defaultVal){
        final Character c = asCharacter(x, null);
        return c == null ? (defaultVal == null ? 0 : defaultVal) : c;
    }


    /**
     * Convert an object to a specific type.
     * @param x the object to convert
     * @param c the class to convert to
     * @return the converted object or the original object if conversion is not applicable
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static Object as(Object x, Class c){
        if(c.isAssignableFrom(CharSequence.class)){
            return asString(x);
        } else if(c.isAssignableFrom(Double.class)){
            return asDouble(x);
        } else if(c.isAssignableFrom(Integer.class)){
            return asInteger(x);
        } else if(c.isAssignableFrom(Number.class)){
            return asDouble(x);
        } else if(c.isAssignableFrom(Boolean.class)){
            return asBoolean(x);
        }
        return x;
    }

    // Map

    /**
     * Try hard to get a value from a map by key.
     * Get a value from a map by key, coercing the key to the type of the first key in the map.
     * If the map is null or empty, or the key is null, return the default value.
     * @param map the map to get the value from
     * @param key the key to look for
     * @param defaultValue the value to return if the key is not found
     * @return the value associated with the coerced key, or the default value if not found
     */
    @SuppressWarnings("rawtypes")
    public static Object get(Map map, Object key, Object defaultValue){
        if(map == null || map.isEmpty() || key == null){
            return defaultValue;
        }
        Map.Entry firstEntry = (Map.Entry) map.entrySet().iterator().next();
        final Object firstKey = firstEntry.getKey();
        final Object coercedKey = as(key, firstKey.getClass());

        return map.get(coercedKey);
    }

    /**
     * Get an Integer value from a map by key, coercing the key to the type of the first key in the map.
     * If the key is not found, return the default value.
     * @param map the map to get the value from
     * @param key the key to look for
     * @param defaultValue the value to return if the key is not found
     * @return the Integer value associated with the coerced key, or the default value if not found
     */
    public static Integer get(Map map, Object key, Integer defaultValue){
        return asInteger(get(map, key, (Object) defaultValue));
    }

    /**
     * Get a Double value from a map by key, coercing the key to the type of the first key in the map.
     * If the key is not found, return the default value.
     * @param map the map to get the value from
     * @param key the key to look for
     * @param defaultValue the value to return if the key is not found
     * @return the Double value associated with the coerced key, or the default value if not found
     */
    public static Double get(Map map, Object key, Double defaultValue){
        return asDouble(get(map, key, (Object) defaultValue));
    }

    /**
     * Get a String value from a map by key, coercing the key to the type of the first key in the map.
     * If the key is not found, return the default value.
     * @param map the map to get the value from
     * @param key the key to look for
     * @param defaultValue the value to return if the key is not found
     * @return the String value associated with the coerced key, or the default value if not found
     */
    public static String get(Map map, Object key, String defaultValue){
        return asString(get(map, key, (Object) defaultValue));
    }

    /**
     * Get a Boolean value from a map by key, coercing the key to the type of the first key in the map.
     * If the key is not found, return the default value.
     * @param map the map to get the value from
     * @param key the key to look for
     * @param defaultValue the value to return if the key is not found
     * @return the Boolean value associated with the coerced key, or the default value if not found
     */
    public static Boolean get(Map map, Object key, Boolean defaultValue){
        return asBoolean(get(map, key, (Object) defaultValue));
    }

}
