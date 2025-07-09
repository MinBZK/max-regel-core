package io.github.zvasva.maxregel.core.term;

import java.util.List;

/**
 * A term is the fundamental data structure used to represent information.
 * It is an immutable container of data with key-value pairs (an "object").
 * Since this is the container for domain specific values, it makes it
 * the most important field of a {@link Fact}.
 *
 * Specific implementations of Term may use different data structures.
 * For example a {@link java.util.Map} for more dynamic use-cases, or plain objects for stricter scenarios.
 *
 * @author Arvid Halma
 */
public interface Term {

    /**
     * Check if this Term contains a certain field
     * @param key field name
     * @return true if it exists, false otherwise
     */
    boolean has(String key);

    /**
     * Retrieve a field's value
     * @param key field name
     * @return the value or null if the field does not exist.
     */
    Object get(String key);

    /**
     * Yield all field names
     * @return the different names
     */
    List<String> keys();

    /**
     * Checks if the term contains any fields.
     *
     * @return true if there are no fields, false otherwise
     */
    default boolean isEmpty() {
        return keys().isEmpty();
    }

    /**
     * Returns the number of fields in this Term.
     *
     * @return the number of fields
     */
    default int size() {
        return keys().size();
    }
}
