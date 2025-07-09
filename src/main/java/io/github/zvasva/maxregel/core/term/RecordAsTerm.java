package io.github.zvasva.maxregel.core.term;

import io.github.zvasva.maxregel.util.ReflectionUtil;

import java.util.List;

/**
 * RecordAsTerm is like {@link ObjectAsTerm},
 * but uses knowledge of getter names (in particular foo() instead of getFoo())
 * to speed up getting and checking terms using reflection.
 *
 * @author Arvid Halma
 */
public class RecordAsTerm extends AbstractTerm {
    protected final Object obj;

    private int hash = 0xCAFEBABE; // use a non-zero value to indicate uninitialized

    public RecordAsTerm(Object obj) {
        this.obj = obj;
    }

    @Override
    public boolean has(String key) {
        return ReflectionUtil.hasMethodForField(obj, key);
    }

    @Override
    public Object get(String key) {
        return key == null ? null : ReflectionUtil.getValueFromMethod(obj, key);
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
