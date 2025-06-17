package io.github.zvasva.maxregel.core.term;

import io.github.zvasva.maxregel.util.ReflectionUtil;

import java.util.List;

/**
 * A Term with fields derived from a given object's fields using reflection.
 * This class allows checking for the presence of properties,
 * retrieving their values, and listing all available property names.
 * @author Arvid Halma
 */
public class ObjectFieldsAsTerm extends AbstractTerm {
    protected final Object obj;
    private int hash = Integer.MAX_VALUE;

    public ObjectFieldsAsTerm(Object obj) {
        this.obj = obj;
    }

    @Override
    public boolean has(String key) {
        return ReflectionUtil.hasActualField(obj, key);
    }

    @Override
    public Object get(String key) {
        return key == null ? null : ReflectionUtil.getValueFromField(obj, key);
    }

    @Override
    public List<String> keys() {
        return ReflectionUtil.allFieldNames(obj);
    }

    @Override
    public int hashCode() {
        if(hash == Integer.MAX_VALUE)
            this.hash = Terms.hashCode(this); // cache
        return hash;
    }
}
