package io.github.zvasva.maxregel.util;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Helper functions for {@link Collection}s.
 *
 * @author Arvid Halma
 */
public class Collections {


    /**
     * Create a mutable map literal.
     * E.g. o("key_1", 1, "key_2", 2)
     * @param kvs interleaved key-values
     * @return a new map
     */
    public static Map<String, Object> map(Object ... kvs){
        if(kvs == null){
            return new LinkedHashMap<>(0);
        }
        LinkedHashMap<String, Object> m = new LinkedHashMap<>(kvs.length/2);
        for (int i = 0; i < kvs.length - 1; i+=2) {
            m.put(kvs[i].toString(), kvs[i+1]);
        }
        return m;
    }

    /**
     * Apply a function to all values of a map.
     * @param map the original map.
     * @param f the function to apply to all values
     * @return a new map
     * @param <K> key type
     * @param <V> original value type
     * @param <W> new value type
     */
    public static <K, V, W> Map<K, W> mapValues(Map<K, V> map, Function<V, W> f) {
        return map.entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> f.apply(e.getValue())));
    }

    /**
     * Apply a function to all values of a map.
     * @param map the original map.
     * @param f the function to apply to all values
     * @param mapSupplier e.g. ConcurrentHashMap::new
     * @return a new map
     * @param <K> key type
     * @param <V> original value type
     * @param <W> new value type
     */
    public static <K, V, W> Map<K, W> mapValues(Map<K, V> map, Function<V, W> f, Supplier<Map<K, W>> mapSupplier) {
        return map.entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> f.apply(e.getValue()), (a, b) -> b, mapSupplier));
    }

    /**
     * Concatenate two iterables into a single list.
     * @param as first set of values
     * @param bs second set of values
     * @return a new list with all valies
     * @param <T> the type of values
     */
    public static <T> List<T> concat(Iterable<? extends T> as, Iterable<? extends T> bs){
        ArrayList<T> result = new ArrayList<>();
        for (T a : as) {
            result.add(a);
        }
        for (T b : bs) {
            result.add(b);
        }
        return result;
    }

    /**
     * Create a set containing all unique values from two collections.
     * @param as first collection
     * @param bs second collection
     * @return new set
     * @param <T> value type
     */
    public static <T> Set<T> union(Iterable<? extends T> as, Iterable<? extends T> bs){
        Set<T> result = new LinkedHashSet<>();
        for (T a : as) {
            result.add(a);
        }
        for (T b : bs) {
            result.add(b);
        }
        return result;
    }

    /**
     * Create a set of elements that occur in both collections
     * @param as first collection
     * @param bs second collection
     * @return new set
     * @param <T> value type
     */
    public static <T> Set<T> intersection(Collection<T> as, Collection<T> bs){
        LinkedHashSet<T> result = new LinkedHashSet<>(as);
        result.retainAll(bs);
        return result;
    }

    /**
     * The (asymmetric) difference between two collection. All elements of the second collection are removed from the first.
     * @param as first collection
     * @param bs second collection
     * @return new set
     * @param <T> value type
     */
    public static <T> Set<T> difference(Collection<T> as, Collection<T> bs){
        LinkedHashSet<T> result = new LinkedHashSet<>(as);
        result.removeAll(bs);
        return result;
    }

    /**
     * Create a sinlge map out of two maps.
     * The values from b will overwrite the values from a for common keys.
     * @param a first map
     * @param b second map
     * @return a new map
     * @param <K> key type
     * @param <V> value type
     */
    public static <K, V> Map<K, V> merge(Map<K, V> a, Map<K, V> b){
        Map<K, V> result = new LinkedHashMap<>();
        result.putAll(a);
        result.putAll(b);
        return result;
    }

    /**
     * Use the discrete mapping as a {@link Function} object.
     * null is returned for unknown keys.
     * @param map the mapping
     * @return a function object
     * @param <K> key type
     * @param <V> value type
     */
    public static <K, V> Function<K, V> asFunction(final Map<K, V> map) {
        return k -> map.getOrDefault(k, null);
    }

    /**
     * Use the discrete mapping as a {@link Function} object.
     * @param map the mapping
     * @param defaultValue the value returned if a key does not exist
     * @return a function object
     * @param <K> key type
     * @param <V> value type
     */
    public static <K, V> Function<K, V> asFunction(final Map<K, V> map, final V defaultValue) {
        return k -> map.getOrDefault(k, defaultValue);
    }
}
