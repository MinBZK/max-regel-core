package io.github.zvasva.maxregel.util;

import java.util.Collection;
import java.util.Map;

/**
 * Convert values into other types. Like casting but, more fuzzy... JS fuzzy.
 * @author Arvid Halma
 */
public class Coerce {

    //////////////// Boolean ////////////////

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

    public static boolean asBoolean(Object[] xs){
        return xs != null && xs.length > 0;
    }


    public static <T> T firstTrue(T ... ts){
        for (T t : ts) {
            if(asBoolean(t))
                return t;
        }
        return null;
    }

    public static <T> T orElse(T a, T b) {
        return asBoolean(a) ? a : b;
    }

    //////////////// Integer-like ////////////////

    public static Long asLong(Object x){
        return asLong(x, null);
    }

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

    public static long asLongPrimitive(Object x){
        return asLongPrimitive(x, 0);
    }

    public static long asLongPrimitive(Object x, Number defaultVal){
        final Long y = asLong(x, defaultVal);
        return y == null ? 0L : y.longValue();
    }

    public static Integer asInteger(Object x){
        return asInteger(x, null);
    }

    public static Integer asInteger(Object x, Number defaultVal){
        final Long y = asLong(x, defaultVal);
        return y == null ? null : y.intValue();
    }

    public static int asIntPrimitive(Object x){
        return asIntPrimitive(x, 0);
    }

    public static int asIntPrimitive(Object x, Number defaultVal){
        final Long y = asLong(x, defaultVal);
        return y == null ? 0 : y.intValue();
    }

    public static Short asShort(Object x){
        return asShort(x, null);
    }

    public static Short asShort(Object x, Number defaultVal){
        final Long y = asLong(x, defaultVal);
        return y == null ? null : y.shortValue();
    }

    public static short asShortPrimitive(Object x){
        return asShortPrimitive(x, 0);
    }

    public static short asShortPrimitive(Object x, Number defaultVal){
        final Long y = asLong(x, defaultVal);
        return y == null ? 0 : y.shortValue();
    }

    public static Byte asByte(Object x){
        return asByte(x, null);
    }

    public static Byte asByte(Object x, Number defaultVal){
        final Long y = asLong(x, defaultVal);
        return y == null ? null : y.byteValue();
    }

    public static byte asBytePrimitive(Object x){
        return asBytePrimitive(x, 0);
    }

    public static byte asBytePrimitive(Object x, Number defaultVal){
        final Long y = asLong(x, defaultVal);
        return y == null ? 0 : y.byteValue();
    }

    //////////////// Double-like////////////////

    public static Double asDouble(Object x){
        return asDouble(x, null);
    }

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

    public static double asDoublePrimitive(Object x){
        return asDoublePrimitive(x, Double.NaN);
    }

    public static double asDoublePrimitive(Object x, double defaultVal){
        final Double d = asDouble(x, null);
        return d == null ? defaultVal : d;
    }

    public static Float asFloat(Object x){
        return asFloat(x, null);
    }
    public static Float asFloat(Object x, Number defaultVal){
        final Long y = asLong(x, defaultVal);
        return y == null ? null : y.floatValue();
    }

    public static float asFloatPrimitive(Object x){
        return asFloatPrimitive(x, null);
    }

    public static float asFloatPrimitive(Object x, Character defaultVal){
        final Float d = asFloat(x, null);
        return d == null ? (defaultVal == null ? 0f : defaultVal) : d;
    }

    //////////////// String ////////////////

    public static String asString(Object x){
        return asString(x, null);
    }

    public static String asString(Object x, String defaultVal){
        if(x == null)
            return defaultVal;
        return x.toString();
    }

    public static Character asCharacter(Object x){
        return asCharacter(x, null);
    }

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

    public static char asCharPrimitive(Object x){
        return asCharPrimitive(x, null);
    }

    public static char asCharPrimitive(Object x, Character defaultVal){
        final Character c = asCharacter(x, null);
        return c == null ? (defaultVal == null ? 0 : defaultVal) : c;
    }


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
    public static Object get(Map map, Object key, Object defaultValue){
        if(map == null || map.isEmpty() || key == null){
            return defaultValue;
        }
        Map.Entry firstEntry = (Map.Entry) map.entrySet().iterator().next();
        final Object firstKey = firstEntry.getKey();
        final Object coercedKey = as(key, firstKey.getClass());

        return map.get(coercedKey);
    }

    public static Integer get(Map map, Object key, Integer defaultValue){
        return asInteger(get(map, key, defaultValue));
    }

    public static Double get(Map map, Object key, Double defaultValue){
        return asDouble(get(map, key, defaultValue));
    }

    public static String get(Map map, Object key, String defaultValue){
        return asString(get(map, key, defaultValue));
    }

    public static Boolean get(Map map, Object key, Boolean defaultValue){
        return asBoolean(get(map, key, defaultValue));
    }

}
