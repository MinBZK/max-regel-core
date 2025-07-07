package io.github.zvasva.maxregel.core.term;

import java.util.*;


/**
 * Utility class for operating on {@link Term} objects.
 */
public class Terms {

    /**
     * Get first value of first field.
     * @param term the container
     * @return a key or null
     */
    public static String firstKey(Term term){
        return firstKey(term, null);
    }

    /**
     * Get first key of first field.
     * @param term the container
     * @param defaultKey the key in case there are no fields or the term is null
     * @return a key
     */
    public static String firstKey(Term term, String defaultKey){
        if(term == null) {
            return defaultKey;
        }
        List<String> keys = term.keys();
        return keys.isEmpty() ? defaultKey : keys.getFirst();
    }

    /**
     * Get first value of first field.
     * @param term the container
     * @return a value or null
     */
    public static Object first(Term term){
        return first(term, null);
    }

    /**
     * Get first value of first field.
     * @param term the container
     * @param defaultValue the value in case there are no fields or the term is null
     * @return a value
     */
    public static Object first(Term term, Object defaultValue){
        if(term == null) {
            return defaultValue;
        }
        List<String> keys = term.keys();
        return keys.isEmpty() ? defaultValue : term.get(keys.getFirst());
    }

    /**
     * Returns a list of values from a given term.
     *
     * @param t the term from which to extract values
     * @return a list of values in the term
     */
    public static List<Object> values(Term t) {
        List<Object> result = new ArrayList<>();
        for (String key : t.keys()) {
            result.add(t.get(key));
        }
        return result;
    }

    /**
     * Returns a sorted set of keys from a given term.
     *
     * @param t the term from which to extract keys
     * @return a sorted set of keys in the term
     */
    public static Set<String> sortedKeys(Term t){
        return new TreeSet<>(t.keys());
    }

    /**
     * Returns a list of values from a term, sorted by their corresponding keys.
     *
     * @param t the term from which to extract and sort values
     * @return a list of values sorted by their corresponding keys
     */
    public static List<Object> sortedValuesByKey(Term t){
        List<Object> result = new ArrayList<>();
        for (String key : sortedKeys(t)) {
            result.add(t.get(key));
        }
        return result;
    }

    /**
     * Converts a term to a map with original key ordering.
     *
     * @param t the term to convert
     * @return a map representation of the term with sorted keys
     */
    public static Map<String, Object> asMap(Term t){
        Map<String, Object> result = new LinkedHashMap<>();
        for (String key : t.keys()) {
            result.put(key, t.get(key));
        }
        return result;
    }

    /**
     * Converts a term to a map with sorted keys.
     *
     * @param t the term to convert
     * @return a map representation of the term with sorted keys
     */
    public static Map<String, Object> asSortedMap(Term t){
        Map<String, Object> result = new LinkedHashMap<>();
        for (String key : sortedKeys(t)) {
            result.put(key, t.get(key));
        }
        return result;
    }


    /**
     * Creates a new term by combining the fields of two given terms.
     * If both terms contain the same field, the value from the second term is used.
     *
     * @param a the first term with some fields
     * @param b the second term with (possibly) other fields
     * @return a new term combining the fields of both terms
     */
    public static Term union(Term a, Term b) {
        Map<String, Object> content = new LinkedHashMap<>();
        for (String key : a.keys()) {
            content.put(key, a.get(key));
        }
        for (String key : b.keys()) {
            content.put(key, b.get(key));
        }
        return new MapTerm(content);
    }

    /**
     * Creates a new term with fields that are present in both given terms and have the same value.
     *
     * @param a the first term
     * @param b the second term
     * @return a new term with common fields and values
     */
    public static Term intersection(Term a, Term b) {
        Map<String, Object> content = new LinkedHashMap<>();
        for (String k : a.keys()) {
            if(b.has(k)){
                Object v = a.get(k);
                if(Objects.equals(v, b.get(k))){
                    content.put(k, v);
                }
            }
        }
        return new MapTerm(content);
    }

    /**
     * Selects a subset of fields from a given term based on specified keys.
     *
     * @param a the original term
     * @param keys the field names to copy from the original term
     * @return a new term with the selected fields
     */
    public static Term pick(Term a, Collection<String> keys) {
        Map<String, Object> content = new LinkedHashMap<>();
        for (String k : keys) {
            if(a.has(k)){
                content.put(k, a.get(k));
            }
        }
        return new MapTerm(content);
    }

    /**
     * Generates the hash code for a term based on its sorted values.
     *
     * @param t the term to hash
     * @return the hash code of the term
     */
    public static int hashCode(Term t) {
        return sortedValuesByKey(t).hashCode();
    }

    /**
     * Compares two terms for equality.
     * Two terms are considered equal if they have the same keys and corresponding values.
     *
     * @param a the first term to compare
     * @param b the second term to compare
     * @return true if the terms are equal, false otherwise
     */
    public static boolean equals(Term a, Term b) {
        if(a == b) return true;
        Set<String> thisKeys = sortedKeys(a);
        List<String> otherKeys = b.keys();
        if(thisKeys.size() != otherKeys.size() || !thisKeys.containsAll(otherKeys)) {
            return false;
        }
        for (String key : thisKeys) {
            if(!Objects.equals(a.get(key), b.get(key))){
                return false;
            }
        }
        return true;
    }

    /**
     * Converts a term to its string representation.
     *
     * @param t the term to convert
     * @return the string representation of the term
     */
    public static String toString(Term t) {
        return asMap(t).toString();
    }
}
