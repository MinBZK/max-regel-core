package io.github.zvasva.maxregel.core.term;

import java.util.List;

/** A term is a basic domain specific value. An immutable container of data.
 * The most important value of a [io.github.zvazva.core.term.Fact].
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
