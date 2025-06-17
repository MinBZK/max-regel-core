package io.github.zvasva.maxregel.core.process.rule;


/**
 * The AbstractRule class provides a skeletal implementation of the {@link Rule} interface.
 * This abstract class implements the hashCode, equals, and toString methods based on its {@link #ast()}.
 * Subclasses of AbstractTerm must implement the abstract methods defined in the {@link Rule} interface.
 */
public abstract class AbstractRule implements Rule {
    /**
     * Determines the hashCode based on abstract syntax tree: {@link #ast()}
     * @return the hash code
     */
    @Override
    public int hashCode() {
        return Rules.hashCode(this);
    }

    /**
     * Determines equality based on the abstract syntax tree: {@link #ast()}
     * @return true if the ASTs are the same
     */
    @Override
    public boolean equals(Object obj) {
        return Rules.equals(this, obj);
    }

    /**
     * String based on the: {@link #ast()}
     * @return (nested) ast string
     */
    @Override
    public String toString() {
        return Rules.toString(this);
    }
}
