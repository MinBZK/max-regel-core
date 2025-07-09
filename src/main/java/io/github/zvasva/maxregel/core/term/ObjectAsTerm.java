package io.github.zvasva.maxregel.core.term;

import io.github.zvasva.maxregel.util.ReflectionUtil;

import java.util.List;

/**
 * A Term with fields derived from a given object's getter methods or fields using reflection.
 * This class allows checking for the presence of properties,
 * retrieving their values, and listing all available property names.
 *
 * @author Arvid Halma
 */
public class ObjectAsTerm extends AbstractTerm {
    protected final Object obj;

    private int hash = 0xCAFEBABE; // use a non-zero value to indicate uninitialized

    public ObjectAsTerm() {
        this.obj = this;
    }

    public ObjectAsTerm(Object obj) {
        this.obj = obj;
    }

    @Override
    public boolean has(String key) {
        return ReflectionUtil.hasField(obj, key);
    }

    @Override
    public Object get(String key) {
        return key == null ? null : ReflectionUtil.getValue(obj, key);
    }

    @Override
    public List<String> keys() {
        return ReflectionUtil.allFieldNames(obj);
    }

    @Override
    public int hashCode() {
        if(hash == 0xCAFEBABE)
            this.hash = Terms.hashCode(this); // the hash is calculated on demand and cached
        return hash;
    }
}
