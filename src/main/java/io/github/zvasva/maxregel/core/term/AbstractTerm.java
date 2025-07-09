package io.github.zvasva.maxregel.core.term;



/**
 * The AbstractTerm class provides a skeletal implementation of the {@link Term} interface.
 * This abstract class implements the hashCode, equals, and toString methods based on Term properties.
 *
 * @author Arvid Halma
 */
public abstract class AbstractTerm implements Term {

    @Override
    public int hashCode() {
        return Terms.hashCode(this);
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj) return true;
        if(obj instanceof Term otherTerm){
            return Terms.equals(this, otherTerm);
        }
        return false;
    }

    @Override
    public String toString() {
        return Terms.toString(this);
    }
}
